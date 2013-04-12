/* 
 * Created on 18.10.2012 - 11:22:42 
 * 
 * Copyright(c) 2012 Thomas Struller-Baumann. All Rights Reserved.
 * This software is the proprietary information of Thomas Struller-Baumann.
 */
function initGraph(graphJSON, width, height, gravity) {
   var force = d3.layout.force()
           .gravity(gravity)
           .size([width, height])
           .distance(80)
           .charge(-height / 2)
           .friction(0.9)
           .theta(0.8)
           .linkDistance(160);


   d3.json(graphJSON, function(json) {
      force.nodes(json.nodes)
              .links(json.links)
              .on("tick", tick)
              .start();

      var svg = d3.select("#canvasGraph").append("svg:svg")
              .attr("width", width)
              .attr("height", height);

      svg.append("svg:defs").selectAll("marker")
              .data(["inject", "event", "produces", "ejb", "instance", "observes"])
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
              .attr("r", 7)
              .style("fill", function(d) {
         return fill(d.group);
      })
              .on("mouseover", showNodeInfos)
              .on("mousedown", function(d) {
         d.fixed = true;
      })
              .call(force.drag);

      // Hide NodeInfos when click outside
      $("#canvasGraph").click(function(d) {
         hideNodeInfos(d);
      });

      // Hide NodeInfos when ESC
      $(document).ready(function() {
         $(document).bind('keydown', function(e) {
            if (e.which === 27) {
               hideNodeInfos(e);
            }
         });
      });

      // Show/Hide description
      $("#pop-description-open").fadeOut(0);
      $("#pop-description-open").click(function(d) {
         $("#pop-description-close").fadeIn(450);
         $("#pop-description-open").fadeOut(0);
         $("#pop-description").slideDown(250);
      });
      $("#pop-description-close").click(function(d) {
         $("#pop-description-close").fadeOut(0);
         $("#pop-description-open").fadeIn(450);
         $("#pop-description").slideUp(250);
      });

      // Show/Hide sourcecode
      $("#pop-sourcecode-open").fadeOut(0);
      $("#pop-sourcecode-open").click(function(d) {
         $("#pop-sourcecode-close").fadeIn(450);
         $("#pop-sourcecode-open").fadeOut(0);
         $("#pop-sourcecode").slideDown(250);
      });
      $("#pop-sourcecode-close").click(function(d) {
         $("#pop-sourcecode-close").fadeOut(0);
         $("#pop-sourcecode-open").fadeIn(450);
         $("#pop-sourcecode").slideUp(250);
      });

      function showNodeInfos(d) {
         highlight(0.1, d);
         poppadding = 50;
         $("#pop-up").fadeOut(150, function() {
            $("#pop-up-title").html(d.name);
            $("#pop-description").html(d.description);
            $("#pop-sourcecode").html(d.sourcecode);
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
            $("#pop-up").fadeIn(150);
         });
      }

      function hideNodeInfos(d) {
         highlight(1, d);
         $("#pop-up").fadeOut(150);
         d3.select(this).attr("fill", "url(#ten1)");
      }

      function highlight(opacity, d, o) {
         circle.style("stroke-opacity", function(o) {
            thisOpacity = isConnected(d, o) ? 1 : opacity;
            this.setAttribute('fill-opacity', thisOpacity);
            return thisOpacity;
         });
         circle.transition().attr("r", function(o) {
            if (opacity === 1) {
               thisR = 7;
               this.setAttribute('r', thisR);
            } else {
               thisR = isConnected(d, o) ? 10 : 7;
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

// Make pop-up draggable Begin
      (function($) {
         $.fn.drags = function(opt) {

            opt = $.extend({handle: "", cursor: "move"}, opt);

            if (opt.handle === "") {
               var $el = this;
            } else {
               var $el = this.find(opt.handle);
            }

            return $el.css('cursor', opt.cursor).on("mousedown", function(e) {
               if (opt.handle === "") {
                  var $drag = $(this).addClass('draggable');
               } else {
                  var $drag = $(this).addClass('active-handle').parent().addClass('draggable');
               }
               var z_idx = $drag.css('z-index'),
                       drg_h = $drag.outerHeight(),
                       drg_w = $drag.outerWidth(),
                       pos_y = $drag.offset().top + drg_h - e.pageY,
                       pos_x = $drag.offset().left + drg_w - e.pageX;
               $drag.css('z-index', 1000).parents().on("mousemove", function(e) {
                  $('.draggable').offset({
                     top: e.pageY + pos_y - drg_h,
                     left: e.pageX + pos_x - drg_w
                  }).on("mouseup", function() {
                     $(this).removeClass('draggable').css('z-index', z_idx);
                  });
               });
               e.preventDefault(); // disable selection
            }).on("mouseup", function() {
               if (opt.handle === "") {
                  $(this).removeClass('draggable');
               } else {
                  $(this).removeClass('active-handle').parent().removeClass('draggable');
               }
            });

         }
      })(jQuery);
      $('#pop-up').drags();
// Make pop-up draggable End

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
         // return d.source.name + " - " + d.target.name;
         return d.type;
      });

      function tick() {
         path.attr("d", function(d) {
            var dx = d.target.x - d.source.x;
            var dy = d.target.y - d.source.y;
            var dr = Math.sqrt(dx * dx + dy * dy) * 3;  //*3 for a flatter curve
            return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
         });

         label.attr("transform", function(d) {
            var dx = d.target.x - d.source.x;
            var dy = d.target.y - d.source.y;

            if (d.source.x < d.target.x) {
               dx = d.source.x + (d.target.x - d.source.x) / 2;
            } else {
               dx = d.target.x + (d.source.x - d.target.x) / 2;
            }

            if (d.source.y < d.target.y) {
               dy = d.source.y + (d.target.y - d.source.y) / 2;
            } else {
               dy = d.target.y + (d.source.y - d.target.y) / 2;
            }

            return "translate(" + dx + "," + dy + ")";
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
         //########### Hack um Force-Directed besser den Graphen aufzuspannen

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
         if (maxWidth < 500) {
            maxWidth = 500;
         }
         if (maxHeight < 500) {
            maxHeight = 500;
         }
         svg.attr("width", maxWidth + 200).attr("height", maxHeight + 100);
      }

      function isConnected(a, b) {
         return linkedByIndex[a.index + "," + b.index] || linkedByIndex[b.index + "," + a.index] || a.index === b.index;
      }

   });
}