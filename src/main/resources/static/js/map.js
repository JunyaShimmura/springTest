// 地図オブジェクトを保持する変数
let leafletMap = null;
/**
 * 地図を表示するメイン関数
 */
function displayMap() {
    console.log("位置情報取得を開始します...");

    // ブラウザが位置情報に対応しているか確認
    if (!navigator.geolocation) {
        alert("お使いのブラウザは位置情報に対応していません。");
        return;
    }

    // 位置情報を取得
    navigator.geolocation.getCurrentPosition(
        function(position) {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;
            console.log("位置取得成功:", lat, lng);

            const mapElement = document.getElementById('map');
            if (!mapElement) {
                console.error("地図を表示する要素（id='map'）が見つかりません。");
                return;
            }

            // --- Leafletの初期化 ---
            // 既に地図が表示されている場合は一旦削除（二重表示エラー防止）
            if (leafletMap !== null) {
                leafletMap.remove();
            }

            // [lat, lng] を中心にズームレベル15で表示
            leafletMap = L.map('map').setView([lat, lng], 15);

            // 地図の絵（タイル）を読み込む（OpenStreetMap）
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenStreetMap contributors'
            }).addTo(leafletMap);

            // ピンを立てる
            L.marker([lat, lng]).addTo(leafletMap);

            // Bootstrap等のレイアウト干渉対策（少し遅れてサイズを再確定させる）
            setTimeout(() => {
                leafletMap.invalidateSize();
            }, 200);

            // 緯度経度をテキストで表示（もしHTMLに要素があれば）
            const textElem = document.getElementById("locationText");
            if (textElem) {
                textElem.innerText = `緯度: ${lat.toFixed(5)} / 経度: ${lng.toFixed(5)}`;
            }
            document.getElementById("lat").value = lat;
            document.getElementById("lng").value = lng;
        },
        function(error) {
            console.error("GPS取得エラー:", error);
            let msg = "位置情報の取得に失敗しました。";
            if (error.code === 1) msg = "位置情報の利用が許可されていません。設定を確認してください。";
            alert(msg);
        },
        {
            enableHighAccuracy: true, // 精度重視
            timeout: 10000,           // 10秒でタイムアウト
            maximumAge: 0             // 常に最新を取得
        }
    );
}

/**
 * ページ読み込み時の処理
 */
document.addEventListener('DOMContentLoaded', function() {
    // ログイン直後の時だけ自動実行する場合（HTMLのdata属性を確認）
    const justLogin = document.body.dataset.justLogin;
//    if (justLogin === "true") {
//        displayMap();
//    }
});