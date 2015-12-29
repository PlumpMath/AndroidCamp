function insert(item, user, request) {
	    request.execute({
	        success: function() {
	            // Write to the response and then send the notification in the background
	            request.respond();
                
                var payload = {
                    data: {
                        message: item.text 
                    }
                }; 
                
	            push.gcm.send(null, payload, {
	                success: function(response) {
	                    console.log('Push notification sent: ', response);
	                }, error: function(error) {
	                    console.log('Error sending push notification: ', error);
	                }
	            });
	        }
	    });
	}