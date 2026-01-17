let allRecords = [];
var today = new Date();

document.addEventListener("DOMContentLoaded", async() =>{
    // 月の選択肢設定
    const monthSelect = document.getElementById("month");
    for (let i = 1; i <=12; i++) {
        const option = document.createElement("option");
        option.value = i;
        option.textContent = `${i}月`;
        monthSelect.appendChild(option);
    }
    // 勤務記録を取得（json バックエンドと通信）
    try {
       const res = await fetch("/api/work-records");
       allRecords = await res.json();
        //初期表示　今月
       filterByMonthTable(today.getMonth()+1);
       // 月が変更されたとき
       monthSelect.addEventListener("change", () => {
         const month = Number(monthSelect.value);
         filterByMonthTable(month);
        });
    } catch (e) {
        console.error("勤務記録の取得に失敗しました。",e);
    }
} );

//選択された月の記録を動的に表示
function filterByMonthTable(month) {
    const tbody = document.querySelector("#workRecordTable tbody");
    const textShowMonth = document.getElementById ("showMonth");
    //  tbody　初期化
    tbody.innerHTML = "";
    // 選択された月を表示
    const year = today.getFullYear();
    textShowMonth.textContent = `${year}年${month}月`;
    //　選択された月の記録を取得
    const filtered = allRecords.filter(record => {
        const date = new Date(record.lowDateTime);
        return date.getMonth() + 1 === month;
    });
    if (filtered.length === 0) {
        const row = document.createElement("h4");
        row.innerHTML =`<h4>この月の勤務記録はありません。</h4>`;
        tbody.appendChild(row);
        return;
    }
    // フィルター後のデータを表示　tr td タグ追加
    filtered.forEach(record => {
    const row = document.createElement("tr");
    row.innerHTML = `
    <td>
        <form th:action="@{/work_recordsAdmin/cancel/{id} (id=${record.id})}" method="post" >
            <button type="submit">削除</button>
        </form>
    </td>
    <td>${record.clockInTime}</td>
    <td>${record.clockOutTime}</td>
    `;
    tbody.appendChild(row);
    });
}
//  //動的表示例
//function filterByMonth(month) {
//  const resultDiv = document.getElementById("result");
//  resultDiv.innerHTML = "";
//
//  const filtered = allRecords.filter(record => {
//    const date = new Date(record.lowDateTime);
//    return date.getMonth() + 1 === month;
//  });
//
//  if (filtered.length === 0) {
//    resultDiv.textContent = "この月の勤務記録はありません。";
//    return;
//  }
//  filtered.forEach(record => {
//    const div = document.createElement("div");
//    div.textContent = `${record.date} : ${record.clockInTime}: ${record.clockOutTime}`;
//    resultDiv.appendChild(div);
//  });
//
//}