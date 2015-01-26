  $(document).ready(function()
	{ 
	  
	} ) ;

   function control()
   {
	   if($("#btnControl").val() == 'start')
	   {
		   $("#btnControl").val("stop");
	   }
	   else
	   {
		   $("#btnControl").val("start");
	   }
	  
	   $.ajax({
	    	 url:'/calgarysensor/initializeSensor.do',
	    	 type:'post',
	    	 success:function(data)
	    	 {
	    		 displayData('init success');
	    		 if( $("#btnControl").val() == "stop")
		   	      {
		   	      	joinLeave("joinStream");
		   	      }
		   	     else
		   	     {
		   	     	joinLeave("leave");
		   	     }
		     }
	    });
   }
   
   function joinLeave(what) 
	{
		if (what == 'joinStream') 
		{
		  //p_join_listen(null, 'stream');
		  
		  PL._init(); 
		  PL.joinListen('/calgary/sensorInfoService');
		}  
		else if (what == 'leave')
		 {
		  p_leave();
		  displayData('无消息或离开状态');
	    }
	}
	
	function displayData(aString)
	 {
		var str = $("#textarea").val();
		$("#textarea").val(str+"\n\r"+aString+"\n\r");
	}	
	
	function onData(event) 
	{ 
		displayData(event.get("sensorinfo"));
	}
