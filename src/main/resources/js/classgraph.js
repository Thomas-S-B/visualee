/*
 * #%L
 * visualee
 * %%
 * Copyright (C) 2013 Thomas Struller-Baumann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
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
var force;
var svg;
var fontSize = 90;
var circleRNormal = 8;
var circleRSelected = 11;
var MIN_SIZE = 300;
var cdiTypeKeys = [];
var cdiTypes = new Array();
cdiTypes["INJECT"] = "Is injected in";
cdiTypes["EVENT"] = "Fires event";
cdiTypes['OBSERVES'] = "Observes for";
cdiTypes['INSTANCE'] = "Injected instance";
cdiTypes['PRODUCES'] = "Produces";
cdiTypes['RESOURCE'] = "Resource";
cdiTypes['EJB'] = "EJB";
cdiTypes['ONE_TO_MANY'] = "One to many >>";
cdiTypes['ONE_TO_ONE'] = "One to one >>";
cdiTypes['MANY_TO_ONE'] = "Many to one >>";
cdiTypes['MANY_TO_MANY'] = "Many to many >>";

function setDistance(newDistance) {
   force.distance(newDistance)
           .start();
}

function setLinkdistance(newLinkdistance) {
   force.linkDistance(newLinkdistance)
           .start();
}

function setGravity(newGravity) {
   force.gravity(newGravity / 2000).start();
}

function setGraphSize(newSize) {
   force.size([newSize, newSize]).start();
}

function setFontSize(newSize) {
   $('body').css('font-size', newSize + "%");
   fontSize = newSize;
   force.start();
}

function initCDITypeKeys() {
   var cdiTypeKeyIndex = 0;
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

      fill = d3.scale.category20();
      var linkedByIndex = {};
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
              .on("click", function(d) {
         d.fixed = !d.fixed;
      })
              .call(force.drag);

      // Show/Hide tweak graph
      $("#tweakgraph-open").fadeOut(0);
      $("#tweakgraph-open").click(function(d) {
         $("#tweakgraph-close").fadeIn(450);
         $("#tweakgraph-open").fadeOut(0);
         $("#tweakgraph-sliders").slideDown(250);
      });
      $("#tweakgraph-close").click(function(d) {
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

      // Hide NodeInfos when click outside
      /*
       $("body").click(function(d) {
       hideNodeInfos(d);
       });
       */
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

      function showNodeInfos(d) {
         highlight(0.1, d);
         // poppadding = 50;
         $("#pop-up").fadeOut(150, function() {
            $("#pop-up-title").html(d.name);
            $("#pop-description").html(d.description);
            $("#pop-sourcecode").html(d.sourcecode);
            /*
             if (d.x < $(window).width() / 2) {
             popLeft = d.x + poppadding;
             } else {
             popLeft = d.x - $("#pop-up").width() - poppadding;
             }
             if (popLeft < 0) {
             popLeft = poppadding;
             }
             popTop = d.y + 100;
             if (popTop > $(window).height() - $("#pop-up").height()) {
             popTop = $(window).height() - $("#pop-up").height() - poppadding;
             }
             $("#pop-up").css({
             "left": popLeft,
             "top": popTop
             });
             */
            $("#pop-up").fadeIn(150);
         });
         updatePopUpSize();
      }

      function updatePopUpSize() {
         $("#pop-sourcecode").width($("#pop-up").width());
         var p = document.getElementById('pop-sourcecode');
         $("#pop-sourcecode").height($("#pop-up").height() - p.offsetTop);
      }

      function hideNodeInfos(d) {
         highlight(1, d);
         $("#pop-up").fadeOut(150);
      }

      function highlight(opacity, d, o) {
         circle.style("stroke-opacity", function(o) {
            thisOpacity = isConnected(d, o) ? 1 : opacity;
            this.setAttribute('fill-opacity', thisOpacity);
            return thisOpacity;
         });
         circle.transition().attr("r", function(o) {
            if (opacity === 1) {
               thisR = circleRNormal;
               this.setAttribute('r', thisR);
            } else {
               thisR = isConnected(d, o) ? circleRSelected : circleRNormal;
               this.setAttribute('r', thisR);
            }
            return thisR;
         });

         text.style("stroke-opacity", function(o) {
            thisOpacity = isConnected(d, o) ? 1 : opacity;
            this.setAttribute('fill-opacity', thisOpacity);
            return thisOpacity;
         });

         path.style("stroke-opacity", function(o) {
            return o.source === d || o.target === d ? 1 : opacity;
         });

         label.style("stroke-opacity", function(o) {
            thisOpacity = isConnected(d, o) ? 1 : opacity;
            this.setAttribute('fill-opacity', thisOpacity);
            return thisOpacity;
         });

      }

      var text = svg.append("svg:g").selectAll("g")
              .data(force.nodes())
              .enter().append("svg:g");
      text.append("svg:text")
              .attr("x", 10)
              .attr("y", ".31em")
              .style("font-size", "110%")
              .text(function(d) {
         return d.name;
      });

      var label = svg.append("svg:g").selectAll("label")
              .data(force.links())
              .enter().append("svg:g");
      label.append("svg:text")
              .attr("x", 10)
              .attr("y", ".31em")
              .style("font-size", "80%")
              .text(function(d) {
         return cdiTypes[d.type];
      });

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
         return linkedByIndex[a.index + "," + b.index] || linkedByIndex[b.index + "," + a.index] || a.index === b.index;
      }

   });
}
