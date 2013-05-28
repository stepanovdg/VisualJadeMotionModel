function get_click_position(event){
            var location = event.latLng;
            var lat = location.lat();
            var lng = location.lng();
            app.handle(lat, lng);
 }