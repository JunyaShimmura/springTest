let allRecords = [];

document.addEventListener("DOMContentLoaded", async() =>{
    const monthSelect = document.getElementById("month");
    // 月の選択肢を追加
    for (let i = 1; i <= 12; i++) {
        const option = document.createElement("option");
        option.value = i;
        option.textContent = `${i}月`;
        monthSelect.appendChild(option);
    }
    // 勤務記録を取得（バックエンドと通信）
    try {
       const res = await fetch("/api/work-records");
       allRecords = await res.json();
       filterByMonth(4); // 初期表示（1月）

       // 月が変更されたとき
       monthSelect.addEventListener("change", () => {
         const month = Number(monthSelect.value);
//         filterByMonth(month);
         filterByMonthTable(month);
        });
    } catch (e) {
        console.error("勤務記録の取得に失敗しました。",e);
    }

} );

function filterByMonth(month) {
  const resultDiv = document.getElementById("result");
  resultDiv.innerHTML = "";

  const filtered = allRecords.filter(record => {
    const date = new Date(record.lowDateTime);
    return date.getMonth() + 1 === month;
  });

  if (filtered.length === 0) {
    resultDiv.textContent = "この月の勤務記録はありません。";
    return;
  }
  filtered.forEach(record => {
    const div = document.createElement("div");
    div.textContent = `${record.date} : ${record.clockInTime}: ${record.clockOutTime}`;
    resultDiv.appendChild(div);
  });

}

function filterByMonthTable(month) {
    const tbody = document.querySelector("#workRecordTable tbody");
    //  tbody　初期化
    tbody.innerHTML = "";
    //　選択された月を取得
    const filtered = allRecords.filter(record => {
        const date = new Date(record.lowDateTime);
        return date.getMonth() + 1 === month;
    });
    if (filtered.length === 0) {
        const row = document.createElement("tr");
        row.innerHTML =`<td>この月の勤務記録はありません。</td>`;
        tbody.appendChild(row);
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