const LIMIT_TIME = 10*60*1000; //10分
let todayRecordId,lat,lng, tdClockIn, tdClockOut,recordMg, inBtn, outBtn, cancelBtn;
const FORMAT_TIME = new Intl.DateTimeFormat('ja-JP',{
    hour:'2-digit',
    minute: '2-digit'
});

// ページ読み込み時の処理
document.addEventListener('DOMContentLoaded',async function() {
    lat = document.getElementById("lat").value;
    lng = document.getElementById("lng").value;
    tdClockIn = document.getElementById("table-clock-in-time");
    tdClockOut = document.getElementById("table-clock-out-time");
    recordMg = document.getElementById("recordMg");
    inBtn = document.getElementById("punch-in-btn");
    outBtn = document.getElementById("punch-out-btn");
    cancelBtn = document.getElementById("cancel-btn");
    todayRecordId = document.getElementById("todayRecordId").value;

    if (todayRecordId !== '0') {
        await getWorkRecord(todayRecordId);
    }
    updateButtonUI();
});
//ボタンの色を変える
function updateButtonColor(button, newColorClass) {
    // ボタンが持ちうる色クラスをすべて一旦消す
    const colorClasses = ['btn-success', 'btn-danger', 'btn-warning', 'btn-secondary', 'btn-outline-secondary'];
    button.classList.remove(...colorClasses);
    // 新しい色を入れる
    button.classList.add(newColorClass);
    button.disabled = false;
}
//btn　を押下不可・グレーにする
function disabledBtn(btn) {
    if (btn){
        updateButtonColor(btn,'btn-outline-secondary');
        btn.disabled = true;
    }
}
//ボタン見た目を変える
function updateButtonUI() {
    if (todayRecordId !== "0") {
        updateButtonColor(cancelBtn,"btn-warning");
        disabledBtn(inBtn);
    } else {
        updateButtonColor(inBtn,"btn-success");
        disabledBtn(cancelBtn);
    }
    if (todayRecordId !== "0" && tdClockOut.textContent.trim() === ""){
        updateButtonColor(outBtn,"btn-danger");
    } else {
        disabledBtn(outBtn);
    }
}

async function getWorkRecord(todayRecordId) {
    try {
        const response = await fetch(`/api/attendance/getWorkRecord/${todayRecordId}`, {
            method: 'POST'
        });
        if (response.ok) {
            const result = await response.json();

            if (result.clockInTime){
                displayInTime = new Date(result.clockInTime);
            }
            if (result.clockOutTime) {
                displayOutTime = new Date(result.clockOutTime);
            }
            tdClockIn.textContent = result.clockInTime ? FORMAT_TIME.format(displayInTime) : "";
            tdClockOut.textContent = result.clockOutTime ? FORMAT_TIME.format(displayOutTime) : "";
        } else {
            const errorText = await response.text();
            alert("エラー: " + errorText);
        }
    } catch (error) {
        console.error("通信エラー:", error);
    }
}
async function handlePunchIn() {
    try {
        const response = await fetch('/api/attendance/punch-in', {
            method: 'POST'
        });
        if (response.ok) {
            const result = await response.json();
            todayRecordId = result.id;
            tdClockIn.textContent = FORMAT_TIME.format(new Date(result.clockInTime));
            recordMg.textContent = result.message;
            updateButtonUI();
        } else {
            const errorText = await response.text();
            alert("エラー: " + errorText);
        }
    } catch (error) {
        console.error("通信エラー:", error);
    }
}

async function handlePunchOut() {
    try {
        const res = await fetch(`/api/attendance/punch-out/${todayRecordId}`, {
            method: 'POST'
        });
        if (res.ok) {
            const result = await res.json();
            tdClockOut.textContent = FORMAT_TIME.format(new Date(result.clockOutTime));
            recordMg.textContent = result.message;
            updateButtonUI();
        } else {
            const errorText = await res.text();
            alert("エラー: " + errorText);
        }
    } catch (error) {
        console.error("通信エラー:", error);
    }
}

async function handleCancel() {
    try {
        const response = await fetch(`/api/attendance/cancel/${todayRecordId}`, {
                method: 'POST'
            });
        if (response.ok) {
            const result = await response.json();
            //初期化
            todayRecordId = "0";
            tdClockIn.textContent = "";
            tdClockOut.textContent = "";
            recordMg.textContent = result.message;
            updateButtonUI();
        } else {
            const errorText = await response.text();
            alert("エラー: " + errorText);
        }
    } catch (error) {
        console.error("通信エラー:", error);
    }
}