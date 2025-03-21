function initMap() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {

            var lat = position.coords.latitude;
            var lng = position.coords.longitude;
            document.getElementById("locationText").innerText = `緯度: ${lat}, 経度: ${lng}`;

            var map = new google.maps.Map(document.getElementById('map'), {
                center: { lat: lat, lng: lng },
                zoom: 15
            });

            new google.maps.Marker({
                position: { lat: lat, lng: lng },
                map: map,
                title: "現在地"
            });


        });
    } else {
        alert("位置情報を取得できません");
    }
    document. getElementById('locationText'). textContent = '位置情報取得中...';
}
// ページが完全に読み込まれてから地図を初期化
document.addEventListener('DOMContentLoaded', function() {
    if (typeof initMap === 'function') {
        initMap();
    }
});

function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position) {
                document.getElementById("lat").value = position.coords.latitude;
                document.getElementById("lng").value = position.coords.longitude;
        //      document.getElementById("gpsForm").submit();
                const  locationText = document. getElementById('locationText');
                locationText.textContent = 'lat lng 取得';
            },
            function(error) {
                alert("位置情報をーーー取得できませんでした: " + error.message);
            },
            {
            enableHighAccuracy: true, // GPS優先

            }
        );
    } else {
        alert("このブラウザは----位置情報をサポートしていません。");
    }
}



// DOMが読み込まれた後にイベントリスナーを追加
document.addEventListener("DOMContentLoaded", function() {

});



