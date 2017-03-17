
 (function () {
	"use strict";
	$(window).on('action:app.load', function() {
		if(app.user && app.user.uid != 0){
			
			$.ajax({
				url:  location.origin + config.relative_path + '/api/me/onesignal/devices',
				type: 'post',	
				dataType: 'json',
				data: { 'player_id' : '%s'},
				success: function(data) {},
				error: function(data) {
					console.log('Failed to report OneSignal player id: ' + data);
				}			
			});				
		}	
	});
 }());