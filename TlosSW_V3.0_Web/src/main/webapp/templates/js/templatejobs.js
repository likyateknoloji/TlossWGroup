$(document).ready(function() {
/*
	$('.jobGroup').toggle(
	    function() {
	        if($(this).children().length) $(this).children().show();
	    },
	    function() {
       if($(this).children().length) $(this).children().hide();
	    }
);
*/
$('.jobGroup').hover(
    function() {
        $(this).addClass('active');
    },
    function() {
        $('.container > div').removeClass('active');
    }
);


	function escapeStr(str) {
		if (str)
			return str.replace(/([ #;?,.+*~\':"!^$[\]()=>|\/@])/g, '\\$1');
		else
			return str;
	}

	function findUniqueId(id) {
		var date = new Date();
		var fixedPart = "";

		if (id.lastIndexOf("_") > 0)
			fixedPart = id.substring(0, id.lastIndexOf("_")) + "_";

		return fixedPart + date.getSeconds() + date.getMilliseconds();
	}

	var dragIcon = document.createElement('img');

	dragIcon.src = 'likya_logok.jpg';
	dragIcon.width = 75;

	var dragSrcEl = null;

	var yum = document.createElement('p');
	var eat = [ 'yum!', 'gulp', 'burp!', 'nom' ];
	var msie = /*@cc_on!@*/0;

	yum.style.opacity = 1;

	console.log("All Template jobs");
    
	function handleDragStart(e) {
		this.style.opacity = '0.4'; // this / e.target is the source node.
		e.dataTransfer.effectAllowed = 'copy'; // A copy of the source item may be made at the new location.
		e.dataTransfer.setData('text/html', this.id); // required otherwise doesn't work
	}

	function handleDragOver(e) {
		if (e.preventDefault)
			e.preventDefault(); // allows us to drop
		this.className = 'ui-treenode ui-treenode-leaf job';
		//this.classList.add('over');
		e.dataTransfer.dropEffect = 'copy'; // See the section on the DataTransfer object.
		return false;
	}

	function handleDragEnter(e) {
		this.className = 'ui-treenode ui-treenode-leaf job';
		//this.classList.add('over');
		return false;
	}

	function handleDragLeave(e) {
		this.className = 'ui-treenode ui-treenode-leaf job';
		//this.classList.remove('over');  // this / e.target is previous target element.
	}

	function callHandleDropByName(jobName,jobPath) {
		document.getElementById('jobTemplatesForm:draggedTemplateName').value = jobName;
		document.getElementById('jobTemplatesForm:draggedTemplatePath').value = jobPath;

		callHandleDrop();
	}
	
	//jobin senaryo agacinda birakildigi pathi buluyor
	function getJobPath(node) {
		var treeContainerClassName = "ui-tree-container";
		
		//serbest islerden biriyse path bos donuyor
		if (node.parentNode.className == treeContainerClassName) {
			return "";
		}
		
		var scenarioNode = node.parentNode.previousSibling;
		var jobPath = $(scenarioNode.lastChild).text();
		
		while (scenarioNode.parentNode.parentNode.className != treeContainerClassName) {
			scenarioNode = scenarioNode.parentNode.parentNode.previousSibling;
			
			jobPath = $(scenarioNode.lastChild).text() + "/" + jobPath;
		}
		
		console.log("jobPath : " + jobPath);
		
		return jobPath;
	}

	function handleDrop(e) {
		if (e.stopPropagation)
			e.stopPropagation(); // stops the browser from redirecting...

		var elw = document.getElementById(e.dataTransfer.getData('text/html'));
		console.log("The element which is being dragged !!");
		console.log(elw);

		console.log("The element which is being dropped on it !!");
		console.log(this);

		var newUniqueId = findUniqueId(elw.id);
		elw.id = newUniqueId;
		console.log("newUniqueId of the element " + newUniqueId);
		console.log("");
		console.log(elw);
		
		var str = "#";
		str = str + newUniqueId; //+ " span:eq(3)";
		var escapedId = escapeStr(str + ' >span>span>span');

		console.log("-------str:" + str);
		console.log("-------ss3:" + escapedId);

		//var jobNameId = elw.getElementsByTagName("id");
		console.log("-------ss" + elw.getAttribute("data-rowkey"));
		elw.setAttribute("data-rowkey", newUniqueId);
		console.log("-------ss2" + elw.getAttribute("data-rowkey"));
		this.parentNode.appendChild(elw.cloneNode(true));

		//-------------------------------------------------------------
		//TODO icteki attr lari degistirme problemli. uzerinde calisilacak. Hakan
		var jobNameId = document.querySelectorAll(escapedId);
		console.log("jobNameId" + jobNameId);

		console.log("sonuc" + elw.firstChild);
		
		//-------------------------------------------------------------
		
		//isin adini ve senaryoda eklenecegi pathi alip sunucuya gonderiyor
		var jobName = $(elw.firstChild.lastChild).text();
		console.log("jobName : " + jobName);

		var jobPath = getJobPath(this);
		
		callHandleDropByName(jobName,jobPath);
		
		yum.innerHTML = eat[parseInt(Math.random() * eat.length)];

		var y = yum.cloneNode(true);

		setTimeout(function() {
			var t = setInterval(function() {
			
				if (y.style.opacity <= 0) {
					if (msie) { 
						y.style.display = 'none';
					}
					clearInterval(t);
				} else {
					y.style.opacity -= 0.1;
				}
			}, 50);
		}, 250);

		return false;
	}

	var jobs = document.querySelectorAll('.jobGroup .job'), el = null;
    console.log(jobs);
    
	for ( var i = 0; i < jobs.length; i++) {
		el = jobs[i];
		console.log("BAK!!");
		console.log(el);
		el.setAttribute('draggable', 'true');

		// Events
		el.addEventListener('dragstart', handleDragStart, false);
		
		el.addEventListener('dragover', handleDragOver, false);

		// to get IE to work
		el.addEventListener('dragenter', handleDragEnter, false);

		el.addEventListener('dragleave', handleDragLeave, false);
		el.addEventListener('drop', handleDrop, false);
	}

	console.log("Already defined jobs");
	var scenario = document.querySelectorAll('.scenario .job'), sen = null;
    console.log(scenario);
    
	for ( var i = 0; i < scenario.length; i++) {
		sen = scenario[i];
		console.log("KAK!!");
		console.log(sen);
		sen.setAttribute('draggable', 'true');

		// Events
		sen.addEventListener('dragstart', handleDragStart, false);
		sen.addEventListener('dragover', handleDragOver, false);

		// to get IE to work
		sen.addEventListener('dragenter', handleDragEnter, false);

		sen.addEventListener('dragleave', handleDragLeave, false);
		sen.addEventListener('drop', handleDrop, false);
	}
	
	//bagimlilik tanimlamada kullanilan drag ozelligi burada tanimlaniyor
	//center layouttaki tree icindeki islere drag ozelligi ekleniyor (template tree west layoutta)
	$('.ui-layout-center .ui-treenode-leaf').draggable({  
        helper: 'clone',  
        scope: 'treeScope',  
        zindex: ++PrimeFaces.zindex, 
        opacity: 0.35,
        stack: $('.ui-treenode-leaf'),
        start: function(event, ui) {
             var jobName = $(this.firstChild.lastChild).text();
             document.getElementById('jsTreeForm:draggedJobName').value = jobName;
             
             var jobPath = getJobPath(this);
             document.getElementById('jsTreeForm:draggedJobPath').value = jobPath;
             
             callHandleJobDrop();
        }
     }); 
	
	;

});