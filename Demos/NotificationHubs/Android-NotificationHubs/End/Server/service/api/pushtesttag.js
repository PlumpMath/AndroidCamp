exports.get = function(request, response) {
    var azure = require('azure');
    var notificationHubService = azure.createNotificationHubService('HubName', 
                        'FullConnectionString'); 
        notificationHubService.gcm.send('AndroidUser',
            '{"data":{"msg" : "Hello, Android Users!"}}'
        ,
        function (error)
        {
            if (!error) {
                console.warn("Notification successful");
            }
        }
    );    
    response.send(statusCodes.OK, { message : 'Notification Sent' });
};