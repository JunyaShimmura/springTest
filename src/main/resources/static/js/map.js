let lat = null,lng = null;
const LIMIT_TIME = 10*60*1000; //10分

function initMap() {
    console.log("initMap！");
    //位置情報を取得しセッションに保存
    getLocationSave() ;
    // setTimeoutで描画を遅らせてUIブロック回避
    setTimeout(() => {
        //地図表示処理
        displayMap();
    }, 100)
}
    // ボタンのクリックイベントリスナーを設定
document.addEventListener('DOMContentLoaded', function() {

    // justLogin ログイン直後の時　initMap()実行
    let justLogin =  document.body.dataset.justLogin;  // data-just-login の値を取得
    console.log("justLoginの値:", justLogin);
     if (justLogin === "true") {
        initMap();
     }

});

//位置情報を取得しセッションに保存
function getLocationSave() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position){
                //現在地取得
                lat = position.coords.latitude;
                lng = position.coords.longitude;
                console.log("位置取得：",lat,lng);
                //位置情報をセッションに保存
                sessionStorage.setItem("setLocation",JSON.stringify({lat,lng}));
                sessionStorage.setItem("setLocationTime",Date.now());
            },
            function(error){
                document.getElementById("locationText").innerText="位置情報を取得できませんでした";
            },
            {
                enableHighAccuracy : false,
                timeout:15000,
                maximumAge:60000
            }
        );
    } else {
        alert("位置情報を取得できませんでした: " + error.message);
    }
    document. getElementById('locationText'). textContent = '位置情報取得中...';
}
//取得した現在地を再利用できるかチェック、NGのときは再取得しセッションへセット
function getLocationCheck(){
    let location = sessionStorage.getItem("setLocation");
    let locationGetTime = sessionStorage.getItem("setLocationTime");

    if (location && locationGetTime){
        let elapsedTime = Date.now() - parseInt(locationGetTime) ;
        if (elapsedTime < LIMIT_TIME) {
            console.log("セッションから位置情報を再利用:");
        } else {
            console.log("位置情報の有効期限切れ、再取得");
            sessionStorage.removeItem("setLocation");
            sessionStorage.removeItem("setLocationTime");
            getLocation();
        }
    }
}

//地図表示処理(セッションに保存した位置情報を取得)
function displayMap(){
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(position) {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;

                // 地図の初期化
                const map = L.map('map').setView([lat, lng], 15);

                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; OpenStreetMap contributors'
                }).addTo(map);

                L.marker([lat, lng]).addTo(map).bindPopup('現在地').openPopup();

                // 【重要】描画が完了したタイミングでサイズを確定させる
                // これを入れないと、高さが0になったりボタンに被ったりすることがあります
                setTimeout(() => {
                    map.invalidateSize();
                }, 200);

            }, function(error) {
                console.error("GPSエラー:", error);
            });
        }
//    //セッションに保存した位置情報を取得
//    getLocationCheck();
//    let savedLocation = sessionStorage.getItem("setLocation");
//    let location = JSON.parse(savedLocation);
//    let lat = location.lat;
//    let lng = location.lng;
//    document.getElementById("locationText").innerText = `緯度: ${lat}, 経度: ${lng}` ;
//
//    console.log("位置の描画開始！");
//    var map = new google.maps.Map(document.getElementById('map'), {
//        center: { lat: lat, lng: lng },
//        zoom: 15
//    });
//    new google.maps.marker.AdvancedMarkerElement({
//        position: { lat: lat, lng: lng },
//        map: map,
//        content: "現在地"
//    });
}
//位置情報を取得し引数に応じたURLにsubmit
function submitLocation (button) {
   // 送信設定　引数のボタンに応じたURL
    let url = button.getAttribute("data-url")
    let form = document.getElementById("locationForm");
    form.action = url;
    //セッションに保存した位置情報を取得
    getLocationCheck();
    let savedLocation = sessionStorage.getItem("setLocation");
    if (savedLocation) {
        let location = JSON.parse(savedLocation);
        document.getElementById("lat").value = location.lat;
        document.getElementById("lon").value = location.lng;
    }
    console.log("submit緯度経度 ：",lat,lng);
    form.submit();
}

document.addEventListener("DOMContentLoaded", function() {
    // ボタンのクリックイベントリスナーを設定
});
