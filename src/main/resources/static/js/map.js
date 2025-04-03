function initMap() {
    console.log("位置表示処理を実行！");
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {

            var lat = position.coords.latitude;
            var lng = position.coords.longitude;
            document.getElementById("locationText").innerText = `緯度: ${lat}, 経度: ${lng}`;
            // setTimeoutで描画を遅らせてUIブロック回避
            setTimeout(() => {
                console.log("位置の描画開始！");
                var map = new google.maps.Map(document.getElementById('map'), {
                    center: { lat: lat, lng: lng },
                    zoom: 15
                });
                new google.maps.Marker({
                    position: { lat: lat, lng: lng },
                    map: map,
                    title: "現在地"
                });
            }, 10)
        });
    } else {
        alert("位置情報を取得できません");
    }
    document. getElementById('locationText'). textContent = '位置情報取得中...';
//    {           enableHighAccuracy: false,  // 高精度OFF（速くする）
//                timeout: 15000,              // 15秒でタイムアウト
//                maximumAge: 60000     }      // 1分以内ならキャッシュ利用　　
}
// ボタンのクリックイベントリスナーを設定
document.addEventListener('DOMContentLoaded', function() {
      // ページが完全に読み込まれてから地図を初期化　表示
//    if (typeof initMap === 'function') {
//        initMap();
//    }
    // justLogin ログイン直後の時　initMap()実行
    let justLogin =  document.body.dataset.justLogin;  // data-just-login の値を取得
    console.log("justLoginの値:", justLogin);
     if (justLogin) {
        initMap();
     }
     // getLocationをボタンにバインド
     document.getElementById("getLocationButton").addEventListener("click", getLocation);
});

// 位置情報を取得しフォームに設定
function getLocation(button) {
   // 送信設定　引数のボタンに応じたURL
    let url = button.getAttribute("data-url")
    let form = document.getElementById("locationForm");
    form.action = url;
    //位置取得
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position) {
                // 位置情報をフォームに設定
                document.getElementById("lat").value = position.coords.latitude;
                document.getElementById("lon").value = position.coords.longitude;
                 // 位置情報が取得できたら送信
                form.submit() ;
            },
            function(error) {
                alert("位置情報を取得できませんでした: " + error.message);
                 // 失敗してもフォームを送信
                 form.submit();
            },
            {
            enableHighAccuracy: true, // GPS優先

            }
        );
    } else {
        alert("このブラウザは位置情報をサポートしていません。");
         // 失敗してもフォームを送信
         form.submit();
    }

}
// ボタンに応じた遷移先URLを設定しsubmitする
function submitLocation(button){
    getLocation();
    let url = button.getAttribute("dataUrl")
    let form = document.getElementById("locationForm");
    form.action = url;
    form.submit() ;
}

document.addEventListener("DOMContentLoaded", function() {
    // ボタンのクリックイベントリスナーを設定
});







