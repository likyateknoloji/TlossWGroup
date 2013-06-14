var templateJobsDraggable = true;

function activateDraggable() {
	// alert("activateDraggable");
	templateJobsDraggable = true;
	applyDragDrop();
}

function applyDragDrop() {
					// alert("başta2");
	                var jobs_ = document.querySelectorAll('[data-nodetype="jobGroup"] [data-nodetype="job"]');
	                var debug = false;
	                
	                console.log('#####################################');
	                
                    // .hover( handlerIn(eventObject), handlerOut(eventObject) ) Bind two handlers to the matched elements, to be executed when the mouse pointer enters and leaves the elements.
					$( '[data-nodetype="job"], [data-nodetype="scenario"], .jobGroup, .scenario, .job').hover(
							function() {
						      $(this).addClass('active');
					        }, 
					        function() {
					          $(this).removeClass('active');
					        }
					);

					function escapeStr(str) {
						if (str)
							return str.replace(
									/([ #;?,.+*~\':"!^$[\]()=>|\/@])/g, '\\$1');
						else
							return str;
					}

					function findUniqueId(id) {
						var date = new Date();
						var fixedPart = "";

						if (id.lastIndexOf("_") > 0)
							fixedPart = id.substring(0, id.lastIndexOf("_"))
									+ "_";

						return fixedPart + date.getSeconds()
								+ date.getMilliseconds();
					}

					function handleDragStart(e) {
						this.style.opacity = '0.4'; // this / e.target is the source node.
						e.dataTransfer.effectAllowed = 'copy';        // A copy of the source item may be made at the new location.
						e.dataTransfer.setData('text/html', this.id); // required otherwise doesn't work.

					    //this.addClassName('moving');
					    this.classList.add('moving');
						dragSrcEl_ = this;
						//e.dataTransfer.setData('text/html', this.innerHTML);
					}
					
					function handleDragOver(e) {
						if (e.preventDefault)
							e.preventDefault(); // allows us to drop

						e.dataTransfer.dropEffect = 'copy'; // See the section on the DataTransfer object.
						if (debug) console.log('handleDragOver');
						return false;
					}

					function handleDragEnter(e) {
						if (debug) console.log('handleDragEnter');
						//this.addClassName('over');
						this.classList.add('over');
						return false;
					}

					function handleDragLeave(e) {
						// this/e.target is previous target element.
						//this.removeClassName('over');
						this.classList.remove('over'); // this / e.target is previous target element.
						if (debug) console.log('handleDragLeave');
					}

					function handleDrop(e) {
						if (e.stopPropagation)
							e.stopPropagation(); // stops the browser from
													// redirecting...

						// Don't do anything if we're dropping on the same job we're dragging.
                        //if (dragSrcEl_ != this) {
                         //   dragSrcEl_.innerHTML = this.innerHTML;
                         //   this.innerHTML = e.dataTransfer.getData('text/html');
                        //}
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
						str = str + newUniqueId;
						var escapedId = escapeStr(str + ' >span>span>span');

						elw.setAttribute("data-rowkey", newUniqueId);
						
						var node=elw.cloneNode(true);
						var list=this.querySelector('.ui-treenode-children');
						list.insertBefore(node,list.childNodes[0]);
						//(this.querySelector('.ui-treenode-children')).appendChild(elw.cloneNode(true));

						// -------------------------------------------------------------
						// TODO icteki attr lari degistirme problemli. uzerinde
						// calisilacak. Hakan
						// var jobNameId = document.querySelectorAll(escapedId);
						// console.log("jobNameId" + jobNameId);

						// console.log("sonuc" + elw.firstChild);

						// -------------------------------------------------------------

						// isin adini ve senaryoda eklenecegi pathi alip
						// sunucuya gonderiyor
						var jobName = $(elw.firstChild.lastChild).text();
						console.log("jobName : " + jobName);
						console.log("jobPath => " + $(this.firstChild).text());
						var jobPath = getJobPath(this);
						console.log("Full jobPath : " + jobPath + '/'+ jobName);

						callHandleDropByName(jobName, jobPath);

						return false;
					}
					
				    function handleDragEnd(e) {
	                      // this/e.target is the source node.
	                      this.style.opacity = '1';
	                      if (debug) console.log('handleDragEnd');
	                      
	                      [ ].forEach.call(jobs_, function (job) {
	                        //job.removeClassName('over');
	                        job.classList.remove('over');
	                        //job.removeClassName('moving');
	                        job.classList.remove('moving');
	                      });
		                };
				    
					function callHandleDropByName(jobName, jobPath) {
						document.getElementById('jobTemplatesForm:draggedTemplateName').value = jobName;
						document.getElementById('jobTemplatesForm:draggedTemplatePath').value = jobPath;

						//alert("merve1");
						//tekrarlı olarak sürükle-bırak yapılmaması için template işlerin draggable özelliğini kaldırıyoruz
						removeDraggableProperties();
						//alert("ok");
						callHandleDrop();
					}
					
					function removeDraggableProperties() {
						console.log("Template Jobs and Groups");
						//var jobs_ = document.querySelectorAll('.jobGroup .job');
						
						[ ].forEach.call(jobs_, function (job) {
							console.log(job);
							job.setAttribute('draggable', 'false');  // Enable jobs to be draggable.
							// job.removeEventListener('dragstart', handleDragStart, false);
					    });
						
						templateJobsDraggable = false;
					 
					    //$("#west").hide().fadeIn('fast');
					}

					// jobin senaryo agacinda birakildigi pathi buluyor
					function getJobPath(myEl) {
						var treeContainerClassName = "ui-tree-container";
						
						var jobPath = "";
						var first = 1;
						while (myEl.className != treeContainerClassName) {
							if (myEl.getAttribute("data-nodetype") == "scenario"){
								if (first == 1) {
									jobPath = $.trim(myEl.querySelector('li span span span').childNodes[0].nodeValue);
									first = 0;
								} else {
									jobPath = $.trim(myEl.querySelector('li span span span').childNodes[0].nodeValue)+ "/" + jobPath;
								}
								console.log("getJobPath => " + jobPath);
							}

							myEl = myEl.parentNode;
						}

						console.log("********* Birakilan isin senaryo path i *************************");
						// console.log("root parentNode = " + myEl);
						console.log("scenarioPath = " + jobPath);

						var rootOfScenarios = myEl;
						// console.log("rootOfScenarios " + rootOfScenarios);

						// serbest islerden biriyse path bos donuyor
						// if (rootOfScenarios.className ==
						// treeContainerClassName) {
						// return "";
						// }

						return jobPath;
					}

					// jobin senaryo agacindan alindigi pathi buluyor
					function getJobPathForDependency(node) {
						var treeContainerClassName = "ui-tree-container";

						// serbest islerden biriyse path bos donuyor
						if (node.parentNode.parentNode.parentNode.className == treeContainerClassName) {
							return "";
						}

						var scenarioNode = node.parentNode.previousSibling;
						var jobPath = $(scenarioNode.lastChild).text();

						while (scenarioNode.parentNode.parentNode.parentNode.parentNode.className != treeContainerClassName) {
							scenarioNode = scenarioNode.parentNode.parentNode.previousSibling;

							jobPath = $(scenarioNode.lastChild).text() + "/"
									+ jobPath;
						}

						console.log("jobPath : " + jobPath);

						return jobPath;
					}



					console.log("Template Jobs and Groups");
					//var jobs_ = document.querySelectorAll('.jobGroup .job');
					
					if (templateJobsDraggable) {
						
						//alert("surukleme eklendi");
		                   [ ].forEach.call(jobs_, function (job) {
			                 console.log(job);
		                     job.setAttribute('draggable', 'true');  // Enable jobs to be draggable.
		                     job.addEventListener('dragstart', handleDragStart, false);
		                     //job.addEventListener('dragenter', handleDragEnter, false);
		                     //job.addEventListener('dragover', handleDragOver, false);
		                     //job.addEventListener('dragleave', handleDragLeave, false);
		                     //job.addEventListener('drop', handleDrop, false);
		                     //job.addEventListener('dragend', handleDragEnd, false);
		                   });
	                   
					}

						//center layouttaki tree icindeki islere drop ozelligi ekleniyor (template tree west layoutta)				   
						console.log("Already defined Scenarios : #centerWest .scenario");
						var alreadyDefinedjobDef = document.querySelectorAll('[data-nodetype=scenario], #centerWest .scenario'); //document.querySelectorAll('.scenario'); //document.querySelectorAll('#centerWest .scenario .job');

	                   [ ].forEach.call(alreadyDefinedjobDef, function (job) {
		                 console.log(job);
	                     //job.setAttribute('draggable', 'true');  // Enable jobs to be draggable.
	                     //job.addEventListener('dragstart', handleDragStart, false);
	                     job.addEventListener('dragenter', handleDragEnter, false);
	                     job.addEventListener('dragover', handleDragOver, false);
	                     job.addEventListener('dragleave', handleDragLeave, false);
	                     job.addEventListener('drop', handleDrop, false);
	                     job.addEventListener('dragend', handleDragEnd, false);
	                   });
	                   
						//bagimlilik tanimlamada kullanilan drag ozelligi burada tanimlaniyor
						//center layouttaki tree icindeki islere drag ozelligi ekleniyor (template tree west layoutta)				   
						console.log("Already defined Jobs : #centerWest .scenario .job");
						var alreadyDefinedjobDef = document.querySelectorAll('[data-nodetype=scenario] [data-nodetype=job], #centerWest .scenario .job'); //document.querySelectorAll('.scenario'); //document.querySelectorAll('#centerWest .scenario .job');

	                   [ ].forEach.call(alreadyDefinedjobDef, function (job) {
		                 console.log(job);
	                     //job.setAttribute('draggable', 'true');  // Enable jobs to be draggable.
	                     job.addEventListener('dragstart', handleDragStart, false);
	                     //job.addEventListener('dragenter', handleDragEnter, false);
	                     //job.addEventListener('dragover', handleDragOver, false);
	                     //job.addEventListener('dragleave', handleDragLeave, false);
	                     //job.addEventListener('drop', handleDrop, false);
	                     //job.addEventListener('dragend', handleDragEnd, false);
	                   });
						

	                   //bagimlilik tanimlamada kullanilan drag ozelligi burada tanimlaniyor
	                   //center layouttaki tree icindeki islere drag ozelligi ekleniyor (template tree west layoutta)
	                   $('[data-nodetype=scenario] [data-nodetype=job], #centerWest .scenario .job').draggable({
	                	   helper : 'clone',
	                	   scope : 'treeScope',
	                	   opacity : 0.35,
	                	   start : function(event, ui) {
	                		   var jobName = $(this.firstChild.lastChild).text();
	                		   document.getElementById('jsTreeForm:draggedJobName').value = jobName;

	                		   var jobPath = getJobPathForDependency(this);
	                		   document.getElementById('jsTreeForm:draggedJobPath').value = jobPath;

	                		   callHandleJobDrop();
	                	   }
	                   });

	                   ;

}

//$(document).ready(function(){
//	applyDragDrop();
//});