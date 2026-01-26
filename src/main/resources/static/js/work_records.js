let allRecords = [];
var today = new Date();
const monthSelect = document.getElementById("month");

document.addEventListener("DOMContentLoaded", async() =>{
       // 月の選択項目設定
       settingMonthSelect();
       //user の勤務記録を取得
       await fetchWorkRecord();
       //初期表示　今月
       filterByMonthTable(today.getMonth()+1);
} );

// 月の選択項目設定
monthSelect.addEventListener("change", () => {
 const month = Number(monthSelect.value);
 filterByMonthTable(month);
});
 // 月の選択肢設定
function settingMonthSelect() {
    const monthSelect = document.getElementById("month");
    for (let i = 1; i <=12; i++) {
        const option = document.createElement("option");
        option.value = i;
        option.textContent = `${i}月`;
        monthSelect.appendChild(option);
    }
}
//user の勤務記録を取得
async function fetchWorkRecord() {

    // 勤務記録を取得
    try {
       const res = await fetch("/api/attendance/work-records");
       allRecords = await res.json();
    } catch (e) {
        console.error("勤務記録の取得に失敗しました。",e);
    }
}

//選択された月の記録を動的に表示
function filterByMonthTable(month) {
    const tbody = document.querySelector("#workRecordTable tbody");
    const textShowMonth = document.getElementById ("showMonth");
    const recordMg = document.getElementById("recordMg");
    // 初期化
    tbody.innerHTML = "";
    recordMg.textContent = "";
    // 選択された月を表示
    const year = today.getFullYear();
    textShowMonth.textContent = `${year}年${month}月`;
    //　選択された月の記録を取得
    const filtered = allRecords.filter(record => {
        const date = new Date(record.lowDateTime);
        return date.getMonth() + 1 === month;
    });
    if (filtered.length === 0) {
        recordMg.textContent = "この月の勤務記録はありません。";
        return;
    }
    // フィルター後のデータを表示　tr td タグ追加
    filtered.forEach(record => {
    const row = document.createElement("tr");
    row.innerHTML = `
    <td>${record.date}</td>
    <td>${record.clockInTime}</td>
    <td>${record.clockOutTime}</td>
    `;
    tbody.appendChild(row);
    });
}