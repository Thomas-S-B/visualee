/*
 * #%L
 * visualee
 * %%
 * Copyright (C) 2013 Thomas Struller-Baumann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

"use strict";

var force;
var svg;
var transDuration = 150;
var fontSize = 90;
var circleRNormal = 8;
var circleRSelected = 11;
var MIN_SIZE = 300;
var cdiTypeKeys = [];
var cdiTypes = [];
var popupVisible = false;
var searchToken = "";
var dVisible;
cdiTypes.INJECT = "Is injected in";
cdiTypes.EVENT = "Fires event";
cdiTypes.OBSERVES = "Observes for";
cdiTypes.INSTANCE = "Injected instance";
cdiTypes.PRODUCES = "Produces";
cdiTypes.RESOURCE = "Resource";
cdiTypes.EJB = "EJB";
cdiTypes.ONE_TO_MANY = "One to many >>";
cdiTypes.ONE_TO_ONE = "One to one >>";
cdiTypes.MANY_TO_ONE = "Many to one >>";
cdiTypes.MANY_TO_MANY = "Many to many >>";

function searchNode(searchText) {
   searchToken = searchText;
}

function setDistance(newDistance) {
   force.distance(newDistance).linkDistance(newDistance).start();
}

function setGravity(newGravity) {
   force.gravity(newGravity / 2000).start();
}

function setGraphSize(newWidth, newHeight) {
   force.size([newWidth, newHeight]).start();
}

function setFontSize(newSize) {
   $('body').css('font-size', newSize + "%");
   fontSize = newSize;
   force.start();
}

function initCDITypeKeys() {
   var cdiTypeKeyIndex = 0, key;
   for (key in cdiTypes) {
      cdiTypeKeys[cdiTypeKeyIndex] = key;
      cdiTypeKeyIndex++;
   }
}

function initGraph(graphJSON, width, height) {
   initCDITypeKeys();
   force = d3.layout.force()
           .size([width, height])
           .gravity(0.0015)
           .distance(160)
           .charge(-height / 2)
           .linkDistance(160);

   d3.json(graphJSON, function(json) {
      force.nodes(json.nodes)
              .links(json.links)
              .on("tick", tick)
              .start();

      svg = d3.select("#canvasGraph").append("svg:svg")
              .attr("width", width)
              .attr("height", height);

      svg.append("svg:defs").selectAll("marker")
              .data(cdiTypeKeys)
              .enter().append("svg:marker")
              .attr("id", String)
              .attr("viewBox", "0 -5 10 10")
              .attr("refX", 15)
              .attr("refY", -1.5)
              .attr("markerWidth", 6)
              .attr("markerHeight", 6)
              .attr("orient", "auto")
              .append("svg:path")
              .attr("d", "M0,-5L10,0L0,5");

      var path = svg.append("svg:g").selectAll("path")
              .data(force.links())
              .enter().append("svg:path")
              .attr("class", function(d) {
                 return "link " + d.type;
              })
              .attr("marker-end", function(d) {
                 return "url(#" + d.type + ")";
              }
              );

      var text = svg.append("svg:g").selectAll("g")
              .data(force.nodes())
              .enter().append("svg:g");
      text.append("svg:text")
              .attr("x", 10)
              .attr("y", ".31em")
              .style("font-size", "110%")
              .style("text-shadow", "0.1em 0.1em 0.05em #aaa")
              .text(function(d) {
                 return d.name;
              });

      var label = svg.append("svg:g").selectAll("label")
              .data(force.links())
              .enter()
              .append("svg:text")
              .attr("class", "labeltext")
              .attr("x", 10)
              .attr("y", ".31em")
              .style("font-size", "80%")
              .style("text-shadow", "0.05em 0.05em 0.05em #aaa")
              .text(function(d) {
                 return cdiTypes[d.type];
              });

      var fill = d3.scale.category20(), linkedByIndex = {};
      json.links.forEach(function(d) {
         linkedByIndex[d.source.index + "," + d.target.index] = 1;
      });

      var circle = svg.append("svg:g").selectAll("circle")
              .data(force.nodes())
              .enter().append("svg:circle")
              .attr("r", circleRNormal)
              .style("fill", function(d) {
                 return fill(d.group);
              })
              .on("dblclick", showNodeInfos)
              .on("mousedown", function(d) {
                 d.fixed = true;
              })
              .on("mouseover", function(d) {
                 if (!popupVisible) {
                    highlight(0.1, d);
                 }
              })
              .on("mouseout", function(d) {
                 if (!popupVisible) {
                    markSearch();
                 }
              })
              .on("click", function(d) {
                 d.fixed = !d.fixed;
              })
              .call(force.drag);

      // Searching
      $("#searchText").keyup(function() {
         markSearch();
      });
      $("#clearSearch").click(function() {
         markSearch();
      });
      function markSearch() {
         dVisible = [];
         unHighlightAll();
         if (searchToken.length > 0) {
            text.style("fill", function(d) {
               if (stringContains(d.name, searchToken)) {
                  highlightSelected(d);
                  return "red";
               }
               return "black";
            }).style("font-weight", function(d) {
               if (stringContains(d.name, searchToken)) {
                  return "bold";
               }
               return "normal";
            });
         } else {
            highlightAll();
            text.style("fill", function(d) {
               return "black";
            }).style("font-weight", function(d) {
               return "normal";
            });
         }
      }

      // Show/Hide tweak graph
      $("#tweakgraph-open").fadeOut(0);
      $("#tweakgraph-open").click(function() {
         $("#tweakgraph-close").fadeIn(450);
         $("#tweakgraph-open").fadeOut(0);
         $("#tweakgraph-sliders").slideDown(250);
      });
      $("#tweakgraph-close").click(function() {
         $("#tweakgraph-close").fadeOut(0);
         $("#tweakgraph-open").fadeIn(450);
         $("#tweakgraph-sliders").slideUp(250);
      });

      // Make Nodeinfos draggable (using jquery-ui)
      $('#pop-up').draggable({
         cursor: "move",
         handle: "pop-description",
         cancel: "pop-sourcecode"
      });
      $(function() {
         $("#pop-up").resizable();
      });
      $("#pop-up").on("resize", updatePopUpSize);

      $(".pop-up-close").click(function(d) {
         hideNodeInfos(d);
         return false;
      });

      // Hide NodeInfos when ESC
      $(document).ready(function() {
         $(document).bind('keydown', function(e) {
            if (e.which === 27) {
               hideNodeInfos(e);
            }
         });
      });

      function getViewport() {
         var $w = $(window);
         return {
            l: $w.scrollLeft(),
            t: $w.scrollTop(),
            w: $w.width(),
            h: $w.height()
         }
      }
      function showNodeInfos(d) {
         popupVisible = true;
         highlight(0.1, d);
         $("#pop-up").fadeOut(150, function() {
            $("#pop-up-title").html(d.name);
            $("#pop-description").html(d.description);
            $("#pop-sourcecode").html(d.sourcecode);
            $("#pop-up").fadeIn(150);
         });

         // esnure nodeInfos is in the visible viewport
         var position = $("#pop-up").position();
         var width = $("#pop-up").width();
         var height = $("#pop-up").height();
         if (position.left + width > getViewport().l + getViewport().w) {
            $("#pop-up").css({left: getViewport().l});
         }
         if (position.left < getViewport().l) {
            $("#pop-up").css({left: getViewport().l});
         }
         if (position.top + height > getViewport().t + getViewport().h) {
            $("#pop-up").css({top: getViewport().t});
         }
         if (position.top < getViewport().t) {
            $("#pop-up").css({top: getViewport().t});
         }

         updatePopUpSize();
      }

      function updatePopUpSize() {
         $("#pop-sourcecode").width($("#pop-up").width());
         var p = document.getElementById('pop-sourcecode');
         $("#pop-sourcecode").height($("#pop-up").height() - p.offsetTop);
      }

      function hideNodeInfos(d) {
         markSearch();
         $("#pop-up").fadeOut(150);
         popupVisible = false;
      }

      function highlight(opacity, d, o) {
         d3.selectAll("circle").transition().duration(transDuration).style("stroke-opacity", function(o) {
            var thisOpacity = isConnected(d, o) ? 1 : opacity;
            this.setAttribute('fill-opacity', thisOpacity);
            return thisOpacity;
         });

         d3.selectAll("circle").transition().duration(transDuration).attr("r", function(o) {
            var thisR;
            if (opacity === 1) {
               thisR = circleRNormal;
            } else {
               thisR = isConnected(d, o) ? circleRSelected : circleRNormal;
            }
            return thisR;
         });

         text.style("stroke-opacity", function(o) {
            var thisOpacity = isConnected(d, o) ? 1 : opacity;
            this.setAttribute('fill-opacity', thisOpacity);
            return thisOpacity;
         });

         d3.selectAll("path.link").transition().duration(transDuration).style("opacity", function(o) {
            return o.source === d || o.target === d ? 1 : opacity;
         });

         d3.selectAll("text.labeltext").transition().duration(transDuration).style("opacity", function(o) {
            return o.source === d || o.target === d ? 1 : opacity;
         });
      }

      function unHighlightAll(o) {
         setOpacityOfAll(o, 0.1);
      }

      function highlightAll(o) {
         setOpacityOfAll(o, 1);
      }

      function setOpacityOfAll(o, opacity, r) {
         d3.selectAll("circle").style("stroke-opacity", function(o) {
            this.setAttribute('fill-opacity', opacity);
            return opacity;
         });
         d3.selectAll("circle").attr("r", function(o) {
            return circleRNormal;
         });
         text.style("stroke-opacity", function(o) {
            this.setAttribute('fill-opacity', opacity);
            return opacity;
         });
         d3.selectAll("path.link").style("opacity", opacity);
         d3.selectAll("text.labeltext").style("opacity", opacity);
      }

      function highlightSelected(d, o) {
         d3.selectAll("circle").style("stroke-opacity", function(o) {
            if (isConnected(d, o)) {
               this.setAttribute('fill-opacity', 1);
               dVisible.push(o);
               dVisible.push(d);
            }
            return 1;
         });

         text.style("stroke-opacity", function(o) {
            if (isConnected(d, o)) {
               this.setAttribute('fill-opacity', 1);
            }
            return 1;
         });

         d3.selectAll("circle").attr("r", function(o) {
            return circleRNormal;
         });

         d3.selectAll("path.link").style("opacity", function(o) {
            var thisOpacity = this.style.opacity;
            var sourceIsVisible = false;
            for (var i = 0, visible; visible = dVisible[i]; i++) {
               if (o.source === visible) {
                  sourceIsVisible = true;
                  break;
               }
            }
            var targetIsVisible = false;
            for (var i = 0, visible; visible = dVisible[i]; i++) {
               if (o.target === visible) {
                  targetIsVisible = true;
                  break;
               }
            }
            if (sourceIsVisible && targetIsVisible) {
               thisOpacity = 1;
            }
            return thisOpacity;
         });

         d3.selectAll("text.labeltext").style("opacity", function(o) {
            var thisOpacity = this.style.opacity;
            var sourceIsVisible = false;
            for (var i = 0, visible; visible = dVisible[i]; i++) {
               if (o.source === visible) {
                  sourceIsVisible = true;
                  break;
               }
            }
            var targetIsVisible = false;
            for (var i = 0, visible; visible = dVisible[i]; i++) {
               if (o.target === visible) {
                  targetIsVisible = true;
                  break;
               }
            }
            if (sourceIsVisible && targetIsVisible) {
               thisOpacity = 1;
            }
            return thisOpacity;
         });
      }


      function stringContains(inputString, stringToFind) {
         if (stringToFind.length < 1) {
            return false;
         }
         return (inputString.toUpperCase().indexOf(stringToFind.toUpperCase()) !== -1);
      }

      function tick() {
         var dx;
         var dy;
         var dr;
         path.attr("d", function(d) {
            dx = d.target.x - d.source.x;
            dy = d.target.y - d.source.y;
            dr = Math.sqrt(dx * dx + dy * dy) * 3;  //*3 for a flatter curve
            return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
         });

         label.attr("transform", function(d) {
            var dx;
            var dy;
            var offsetDivider = 8;

            if (d.type === 'ONE_TO_ONE') {
               offsetDivider = 5;
            }
            if (d.type === 'ONE_TO_MANY') {
               offsetDivider = 3;
            }

            if (d.source.x < d.target.x) {
               dx = d.source.x + (d.target.x - d.source.x) / offsetDivider;
            } else {
               dx = d.source.x - (d.source.x - d.target.x) / offsetDivider;
            }

            if (d.source.y < d.target.y) {
               dy = d.source.y + (d.target.y - d.source.y) / offsetDivider;
            } else {
               dy = d.source.y - (d.source.y - d.target.y) / offsetDivider;
            }

            // Move label closer to the curve
            if (d.source.x < d.target.x) {
               dy = dy - 10;
            } else {
               dy = dy + 10;
            }

            // Rotate label closer to the curve
            var rotateCloserToCurve = 26;
            var rotate;
            if (d.source.x < d.target.x) {
               rotate = Math.atan2(d.target.y - d.source.y - rotateCloserToCurve, d.target.x - d.source.x) * 180 / Math.PI;
            } else {
               rotate = Math.atan2(d.target.y - d.source.y + rotateCloserToCurve, d.target.x - d.source.x) * 180 / Math.PI;
            }

            return "translate(" + dx + "," + dy + ") rotate(" + rotate + ")";
         });

         circle.attr("transform", function(d) {
            return "translate(" + d.x + "," + d.y + ")";
         });

         text.attr("transform", function(d) {
            return "translate(" + d.x + "," + d.y + ")";
         });

         adjustCanvasSize();
      }

      function adjustCanvasSize() {
         // Hack to force the Force-Directed-Layout to stretch better the graph
         // Node with the lowest x and node with the lowest y
         var minXNode = null;
         var minYNode = null;
         var minWidth = 99999;
         var minHeight = 99999;
         force.nodes().forEach(function(d) {
            if (d.x < minWidth) {
               minWidth = d.x;
               minXNode = d;
            }
            if (d.y < minHeight) {
               minHeight = d.y;
               minYNode = d;
            }
         });
         // Node with the lowest x must be on the left border
         // Node with the lowest y must be on the top border
         minXNode.x = 0;   // 0, to indirect mark this node as the lowest
         minYNode.y = 0;
         //  Hack End

         var maxWidth = 0;
         var maxHeight = 0;
         force.nodes().forEach(function(d) {
            if (d.x > maxWidth) {
               maxWidth = d.x;
            }
            if (d.y > maxHeight) {
               maxHeight = d.y;
            }
            if (d.x < 20) {
               d.x = 20;
            }
            if (d.y < 20) {
               d.y = 20;
            }
         });
         if (maxWidth < MIN_SIZE) {
            maxWidth = MIN_SIZE;
         }
         if (maxHeight < MIN_SIZE) {
            maxHeight = MIN_SIZE;
         }
         var xBorder = fontSize * 2;   //hack
         if (xBorder < 200) {
            xBorder = 200;
         }
         svg.attr("width", maxWidth + xBorder).attr("height", maxHeight + 100);
      }

      function isConnected(a, b) {
         var connected = false;
         if (a === undefined || b === undefined) {
         } else {
            connected = linkedByIndex[a.index + "," + b.index] || linkedByIndex[b.index + "," + a.index] || a.index === b.index;
         }
         return connected;
      }

   });
}
