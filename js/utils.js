/*
 * Tools
 */

var console = console||{log:function(){}};

var $body = $("body");
var $window = $(window);
var $document = $(document);
var $page = $("#page");


String.prototype.startsWith=function(prefix){
	return this.indexOf(prefix)===0;
};

Math.sqr = function(x){
	return x*x;
}

Array.prototype.remove = function() {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};

Array.prototype.pushUnique = function(element){
	if (element.x && element.y){
		for (var i=0;i<this.length;i++)
			if (this[i].x===element.x && this[i].y===element.y)
				return;
	}
	this.push(element);
};

Array.prototype.isEmpty = function(){
	return this.length==0;
}

Array.prototype.foreach = function(callback){
	var len = this.length;
	while (len--)
		try{
			callback(this[len]);
		} catch (e){
			console.error(e);
		}
}

var Tools={
		screenWidth:-1,
		screenHeight:-1,
		page:$body,
		pageCss:{},
		supressNextHistoryEvent:false,
		formatStardate:function(stardate){
			return (Math.round(Computer.stardate*10)/10).toFixed(1);
		},
		scrollIntoView:function(element){
			var offset = element.offset();
			var destination = offset.top;
			$document.scrollTop(destination);
		},
		centerScreen:function(){
			var doc = window.document;
			var delement = doc.documentElement;
			window.scrollTo(0, 0);
		},
		distance:function(x1,y1,x2,y2){
			return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
		},
		extractPositionFrom:function(text){
			var parts = /.*?(\d)[,_](\d)/.exec(text);
			if (parts)
				return {x:parseInt(parts[1]),y:parseInt(parts[2])};
			return null;
		},
		random:function(max){
			max++;
			var e = Math.exp(1);
			return Math.floor(max*(Math.exp(Math.random())-1)/e);
		},
		walkLine:function(x0,y0,x1,y1, callback){
			var sx=0;
			var sy=0;
			var err = 0;
			var e2 = 0;
			var dx = Math.abs(x1-x0);
			var dy = Math.abs(y1-y0); 
			if (x0 < x1) sx = 1; else sx = -1;
			if (y0 < y1) sy = 1; else sy = -1;
			err = dx-dy;

			while(true){
				if (!callback(x0,y0))
					break;
				if (x0 === x1 && y0 === y1) break;
			     e2 = 2*err;
			     if (e2 > -dy){ 
			       err = err - dy;
			       x0 = x0 + sx;
			     }
			     if (e2 <  dx){ 
			       err = err + dx;
			       y0 = y0 + sy;
			     };
				};
		},
		findObstruction:function(quadrant, xFrom,yFrom,xTo,yTo){
			var thing = false;
			var firstStep = true;
			var lastGoodX = xFrom;
			var lastGoodY = yFrom;
			Tools.walkLine(xFrom, yFrom, xTo, yTo, function(x,y){
				if (firstStep)
					firstStep = false;
				else{
					thing = StarMap.getAnythingInQuadrantAt(quadrant, x, y);
					if (thing)
						return false;
					}
				lastGoodX = x;
				lastGoodY = y;
				return true;
			});
			if (thing)
				return {obstacle:thing, x:lastGoodX, y:lastGoodY};
		},
		findPathBetween:function(quadrant,fromX,fromY,toX,toY){
			var graph = [];
			for (var y=0;y<8;y++)
				graph.push([1,1,1,1,1,1,1,1]);
			for (var y=0;y<8;y++)
			for (var x = 0;x<8;x++){
				var thing = StarMap.getAnythingInQuadrantAt(quadrant, x, y);
				if (thing)
					graph[y][x]=0;
			}
			graph[fromY][fromX] = 1;
			graph[toY][toX] = 1;
			graph = new Graph(graph,{diagonal:true});
			var start = graph.grid[fromX][fromY];
			var end = graph.grid[toX][toY];
			var path = astar.search(graph, start, end, { heuristic: astar.heuristics.diagonal });
			return path;
		},
		addCssRule:function(rule){
			var eStyle = $("<style>"+rule+"</style>");
			$("head").append(eStyle);
		},
		removePageCss:function(css){
			if (Tools.hasPageCss(css)){
				Tools.page.removeClass(css);
				delete Tools.pageCss[css];
			}
		},
		addPageCss:function(css){
			if (!Tools.hasPageCss(css)){
				Tools.page.addClass(css);
				Tools.pageCss[css]=true;
			}
		},
		hasPageCss:function(css){
			 return Tools.pageCss.hasOwnProperty(css);
		},
		showScreen:function(screenName){
			var css = Tools.page.attr("class");
			css = css.split(" ");
			var s = "";
			for (var i=0;i<css.length;i++){
				if (!css[i].startsWith("screen"))
					s+=css[i]+" ";
			}
			s+=" screen-"+screenName;
			Tools.page.attr("class",s);
		},
		gotoScreen:function(screen){
			window.location.hash="#"+screen;
		}
};

function isFunction(functionToCheck) {
	 return functionToCheck && {}.toString.call(functionToCheck) === '[object Function]';
}
