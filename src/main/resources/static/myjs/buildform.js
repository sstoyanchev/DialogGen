
//TODO: load the options dynamically

var typesLuisCalendar = [ "",
                          "builtin.calendar.start_time",
                          "builtin.calendar.end_time",
                          "builtin.calendar.start_date",
                          "builtin.calendar.end_date",
                          "builtin.calendar.contact_name",
                          "builtin.calendar.implicit_location",
                          "builtin.calendar.absolute_location",
                          "builtin.calendar.duration",
                          "builtin.calendar.title"];

var typesLuisMortgage = [ "",
                          "builtin.percentage",
                          "builtin.money",
                          "ZipCode"];

var typesLuisGenericSubset = [ "",
                          "builtin.datetime.date",
                          "builtin.datetime.time",
                          "builtin.datetime.duration",
                          "builtin.datetime*",
                          "builtin.geography.city",
                          "builtin.geography.country",
                          //"builtin.geography.pointOfInterest",
                          "builtin.geography*",
                          "builtin.encyclopedia.people*",
                          "builtin.encyclopedia.organization*",
                          "builtin.encyclopedia*"];

var typesCuroHotels = [ "", "GPE","CI","CO","NN"];


//var nluOptions = {"LUIScalendar" : typesLuisCalendar, "LUISgeneric" : typesLuisGenericSubset, "CUROhotels": typesCuroHotels}
var nluOptions = {"LuisMortgage": typesLuisMortgage, "LUISgeneric" : typesLuisGenericSubset, "CUROhotels": typesCuroHotels}


/**
 * 
 */
function getNLUOptions($nlutype)
{
	if(nluOptions[$nlutype]!= undefined)
		return nluOptions[$nlutype];

	return [];
}

/**
 * helper function to create 'option'
 * @param $value
 * @return
 */
function createOption($value)
{
	var $option = document.createElement( 'option' );
	$option.value = $option.textContent =  $value;
	return $option
}


/**
 * Creates a header for the table
 * @param $container
 * @return
 */
			function BuildTableHeader()
			{
				var
				$container = document.getElementById('FormFields');
				
				var $item;
				
				$item = document.createElement('div');
				$item.style.margin = '3px';
				
				$field = document.createElement('span')
				$field.innerHTML = 'index';
				$field.style.marginRight = '10px';
				$item.appendChild($field);
				
				$field = document.createElement('span');
				$field.innerHTML = 'Field Name';
				$field.style.marginRight = '10px';
				$item.appendChild($field);

				$field = document.createElement('span');
				$field.innerHTML = 'Optional?';
				$field.style.marginRight = '10px';
				$item.appendChild($field);
	
				$field = document.createElement('span');
				$field.innerHTML = 'Field Type';
				$field.style.margin = '0px 40px';
				$item.appendChild($field);
	
				/*$field = document.createElement('span');
				$field.innerHTML = 'Must Follow';
				$field.style.margin = '0px 10px';
				$item.appendChild($field);*/

				$field = document.createElement('span');
				$field.innerHTML = 'NOT both';
				$field.style.margin = '0px 10px';
				$item.appendChild($field);
				
				$field = document.createElement('span');
				$field.innerHTML = 'NL Question';
				$field.style.margin = '3px 120px';
				$item.appendChild($field);

				$field = document.createElement('span');
				$field.innerHTML = 'NL implicit Confirmation';
				$field.style.margin = '3px 10px';
				$item.appendChild($field);
				
				$container.appendChild($item);
			}

			/**
			 * adds one field
			 * @param $i - index of the new field, 
			 * @param $container - append content here
			 * @return
			 */
			function CreateOneField($i)
			{
				var
				$item, $field,  $option,
				selectValues = typesLuisGenericSubset;
				
				//get the value of NLU type
				//selectValues = getNLUOPtions(document.getElementById("NLUtype").value);
			
				$item = document.createElement('div');
				$item.style.margin = '3px';
				$item.className='formfield'
				
				$field = document.createElement('span')
				$field.innerHTML = $i.toString();
				$field.style.marginRight = '10px';
				$item.appendChild($field);

				//*************name of the field
				$field = document.createElement('input');
				$field.name = 'fName[' + $i + ']';
				$field.id = 'fName'+$i
				$field.type = 'text';
				$field.style.width = '100px';
				$field.style.marginRight = '10px';
				$item.appendChild($field);
				$field.addEventListener(
					     'change',
						 function(){
					    	 //if nlg$i has not been modified, set the default question
					    	    if($("#nlg"+$i).modified==undefined)
					    	    {
					    	    	$("#nlg"+$i).val("Please specify " + this.value);		
					    	    	updateConstraintsFields();
					    	    	$("#Message").text("setting nlg");	
					    	    }
							    }
					  );

				
				//*************optional yes/no input
				$field = document.createElement('select');
				//$field.disabled = true;
				$field.name = 'b_optional[' + $i + ']';
				$field.style.width = '50px';
				$field.style.marginRight = '10px';
				$item.appendChild($field);
				$field.appendChild(createOption('YES'));
				$field.appendChild(createOption('NO'));
		    	$field.value = "NO";
				
				//*************type of the field
				$field = document.createElement('select');
				$field.name = 'fType[' + $i + ']';
				$field.id = 'fType'+$i;
				$field.type = 'text';
									

				/*$.each(selectValues, function(key,value) {   
					$field.appendChild(createOption(value));
				});*/					
				
				$field.style.width = '160px';
				$field.style.marginRight = '10px';
				$item.appendChild($field);
				
				

				//*************must follow constraint (select one of the previous)
				/*$field = document.createElement('select');
				$field.appendChild(createOption('NONE'));
				
				
				//only previous indexes can go in must follow option
				for ($oIndex = 0; $oIndex < $i; $oIndex++) {
					$field.appendChild(createOption($("#fName"+$oIndex.toString()).val()));						
				}
			
				
				$field.name = 'mustFollow[' + $i + ']';
				$field.id = 'mustFollow' + $i;
				$field.type = 'text';
				$field.style.width = '60px';
				$field.style.marginRight = '10px';					
				$item.appendChild($field);*/
				
				//**************** mutually exclusive constraint
				$field = document.createElement('select');
				$field.addEventListener(
						     'change',
						     checkMutualExclusive,
						     //function() {checkMutualExclusive($i);},
						     false
						  );
				$option = document.createElement( 'option' );
				$option.value = $option.textContent =  'NONE';
				$field.appendChild($option);
				
				
				$field.name = 'mutuallyExclusive[' + $i + ']';
				$field.id = 'mutuallyExclusive'+$i;
				$field.index = $i;
				$field.type = 'text';
				$field.style.width = '80px';
				$field.style.marginRight = '10px';					
				$item.appendChild($field);

				//**************NL question
				$field = document.createElement('input');
				$field.name = 'nlg[' + $i + ']';
				$field.id = 'nlg'+$i;
				$field.type = 'text';
				$field.style.width = '300px';
				$field.style.marginRight = '10px';		
				$field.addEventListener(
					     'change',
						 function(){				  
								this.modified = true;				 
							    $("#Message").text("setting nlg");	
							    }
					  );
				$item.appendChild($field);
				
				//**************NL confirm
				$field = document.createElement('input');
				$field.name = 'implicit[' + $i + ']';
				$field.id = 'implicit'+$i;
				$field.type = 'text';
				$field.style.width = '300px';
				$field.style.marginRight = '10px';		
				$field.addEventListener(
					     'change',
						 function(){				  
								this.modified = true;				 
							    $("#Message").text("confirm question");	
							    }
					  );
				$item.appendChild($field);				
			
				return $item;

			}
			
			/**
			 * Adds fields at the end
			 * @param $num
			 * @return
			 */
			function AddFormFields($amount)
			{
				var
					$container = document.getElementById('FormFields');
				
				var $i;
				
				$numfields = $(".formfield").length;
				
				for ($i = 0; $i < $amount; $i++) {
	                
					$container.appendChild(CreateOneField($numfields+$i));
					resetOneFieldType($numfields+$i);
				
				}
				
			}
			

			
		/*******************************************
		 * called when mutually exclusive is updated
		 * sets the corresponding mutually exclusive as its a reflexive relation
		 * ***************************************/ 
			function checkMutualExclusive(evt)
			{
				var $indexOther = getNamesList().indexOf(evt.target.value);
				var $control = document.getElementById("mutuallyExclusive"+$indexOther+"");
				if($control!=null)
				{
					$control.value =$("#fName"+evt.target.index).val();				
				} 
			}
			

			 /**
			  * for each field, update mutual exclusive list by adding newvalue
			  * @return
			  */
			
			 function updateConstraintsFields()
			 {
				 updatefField("mutuallyExclusive");
				 //updatefField("mustFollow");
			 }
			 
			 function updatefField($fname)
			 {
				 var names = getNamesList()
				 for ($i=0;$i<$(".formfield").length; $i++)
				 {
					 
				 	     var $field = $("#"+ $fname + $i);
				 	     $field.empty();
				 	     $field.append($("<option></option>")
					    		  .attr("value", "NONE").text("NONE"));
				 	     $.each(names, function( $key, $value) {
				 	    	if($("#fName"+$i).val()!=$value)							 
				 	    	$field.append($("<option></option>")
				    		  .attr("value", $value).text($value));
				 	     
				 	     })
					 
				 }
			 }
			 
			 /**
			  * Call-back function when the type of the NLU is reset
			  */
			 function resetType()
			 {
			     var $i;
			     for ($i=0;$i<$(".formfield").length; $i++)
				 {
			    	 resetOneFieldType($i)
				 }
			 }
			 
			 
			 /**
			  * Call-back function when the type of the NLU is reset
			  */
			 function resetOneFieldType($i)
			 {
			 	     var selectValues = getNLUOptions($("#NLUtype").val());
			 	     var $field = $("#fType" + $i)
			    	 $field.empty();
			    	 $.each(selectValues, function(key, value) {
			    		  $field.append($("<option></option>")
			    		     .attr("value", value).text(value));
			    		});

			 }
			 
			 function getIndexName($name)
			 {
				 var $names = getNamesList();
				 var $retval = -1
		    	 $.each($names, function(key, value) {
		    		 if(value==$name)
		    			 ;
		    		});				
		    	 return $retval;
			 }
			 
			 function getNamesList()
			 {
				 var names = [];
			     for ($i=0;$i<$(".formfield").length; $i++)
				 {
			    	 names.push($("#fName" + $i).val());
				 }
			     return names;
			 }
			 
			 function resetForm()
			 {
				 $("#FormFields").html("");
			      BuildTableHeader();		    
			      AddFormFields(2);
			      resetType();
			 }
			 
		     function initializeHotelsExample()
		     {
		    	 $("#FormFields").html("");
		    	 BuildTableHeader();
		    	$("#NLUtype").val("CUROhotels");
		    	resetType();
		        AddFormFields(4);		        
		        $("#greeting").val("Welcome to hotel reservation! When and where do you need a hotel. You can restart the interaction at any time by saying 'restart'.")
		        $("#fName0").val("location"); $("#fType0").val("GPE");$("#nlg0").val("Which city?"); $("#implicit0").val("the hotel in {location}");
		        $("#fName1").val("arrival"); $("#fType1").val("CI");$("#nlg1").val("When are you arriving to {location}?"); $("#implicit1").val("arriving on {arrival}");
		        $("#fName2").val("duration"); $("#fType2").val("NN");$("#nlg2").val("How long do you plan to stay in {location}?");  $("#implicit2").val("staying for {duration}");
		        $("#fName3").val("departure");  $("#fType3").val("CO");$("#nlg3").val("When do you plan to leave?"); $("#implicit3").val("staying until {departure}");
		        updateConstraintsFields();
		        $("#mutuallyExclusive2").val("departure");
		        $("#mutuallyExclusive3").val("duration");

		     }
		     
		     function initializeMortgageExample1()
		     {
		    	 $("#FormFields").html("");
		    	 BuildTableHeader();
		    	$("#NLUtype").val("LuisMortgage");
		    	resetType();
		        AddFormFields(3);		        
		        $("#greeting").val("Welcome to mortgage calculator. Please specify price, downpaymet, and zipcode. You can restart the interaction at any time by saying 'restart'.")
		        $("#fName0").val("price"); $("#fType0").val("builtin.money");$("#nlg0").val("What is the property price?"); $("#implicit0").val("the price is {price}");
		        $("#fName1").val("percentDown"); $("#fType1").val("builtin.percentage");$("#nlg1").val("What is the downpayment percent?"); $("#implicit1").val("downpayment is {percentDown}");
		        $("#fName2").val("zipcode"); $("#fType2").val("ZipCode");$("#nlg2").val("What is the zipcode?");  $("#implicit2").val("zip code is {zipcode}");
		        updateConstraintsFields();

		     }

			 /**
			  * 
			  * @param $json
			  * @return
			  */
			 function createExampleForm($json)
			 {
				 
			 }
