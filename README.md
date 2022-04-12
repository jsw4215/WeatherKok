<h3 align="center"><b>개인 프로젝트, 날씨콕</b></h3>

<h4 align="center">📆 2021.06.10 ~ 2021.10.03</h4>
<br>
<br>

## 📌 프로젝트 소개

- "약속있는 그 날, 그 시간에 날씨가 궁금해?" <br>
- 스케쥴관리와 함께 그날의 날씨예보를 확인해보자!
<br><br> 

<h3><b>🎞 플레이스토어 URL 🎞</b></h3>

[플레이스토어 바로가기](https://play.google.com/store/apps/details?id=com.devpilot.weatherkok)

---

<br>
<h3 align="center"><b>🛠 Tech Stack 🛠</b></h3>
<p align="center">
<img src="https://img.shields.io/badge/Java-0769AD?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/eventBus-181717?style=for-the-badge&logo=eventBus&logoColor=white">
<img src="https://img.shields.io/badge/RxJava-181717?style=for-the-badge&logo=RxJava&logoColor=white">
</br>
<img src="https://img.shields.io/badge/AndroidStudio-F80000?style=for-the-badge&logo=AndroidStudio&logoColor=white">
<img src="https://img.shields.io/badge/Preference-61DAFB?style=for-the-badge&logo=Preference&logoColor=white">

---

<br><br>

<h3 align="center"><b>🏷 API Table 🏷</b></h3>

[기상청 단기예보](https://www.data.go.kr/iim/api/selectAPIAcountView.do)
[기상청 중기예보](https://www.data.go.kr/iim/api/selectAPIAcountView.do)

<br><br>

---

<h3 align="center"><b>✏ Trouble Shooting ✏</b></h3>
<br>
<details>
    <summary>
        <b>선택된 지역에 따른 날씨정보를 단기예보, 중기예보 api를 이용하여 가져오는 도중, 비동기 작업에 의한 오류</b>
    </summary>
    <br>여러 장소를 입력해둘 시, 나중에 요청한 지역 날씨정보가 먼저 신청한 날씨정보보다 먼저 도착하게 되고, 데이터의 수신이 완료된 후,
    <br>메인페이지로 넘어가는 과정에서 데이터 유실이 발생함.
    <br>해결 : eventBus를 이용하여 해당 데이터들의 전송이 완벽하게 완료되면 함수가 실행되도록 변경
</details>
