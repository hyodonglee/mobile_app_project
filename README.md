# ⭐밝히길⭐
## 🧐 프로그램 소개
- '밝히길' 은 밝은 길 최단경로를 찾아주는 어플리케이션입니다. <br>
- 기존의 최단경로 안내 시스템에서 어두운 골목을 지나치게 될 경우, 더욱 밝은 길로 우회하여 갈 수 있도록 추천해주는 시스템입니다.

## 📈 사용한 IDE 와 API

<img src="https://user-images.githubusercontent.com/51476083/99686239-c2802000-2ac6-11eb-901b-a72429d44fc2.png"/>

###### 사용한 Tool 및 API

<hr>

## 🔧 프로그램 구현
🔮 먼저 다음과 같이 API 를 사용하여 출발지(대구광역시 북부 제일교회)부터 목적지(동북로 26길 주택단지)까지 최단경로 좌표를 얻어냅니다.

<img src="https://user-images.githubusercontent.com/51476083/99686944-6ec20680-2ac7-11eb-9ac6-afd0b6279c3e.png"/>

###### 최단경로 좌표
<hr>

🔮 해당하는 출발지부터 목적지까지 최단경로를 표시해줍니다.

<img src="https://user-images.githubusercontent.com/51476083/99690667-bc407280-2acb-11eb-9d19-766e01dcb2c5.png">

###### 최단경로
<hr>

🔮 해당 경로를 포함하는 큰 사각형으로 경계값을 설정해준 후, 이 경계값 안에 포함되는 지역에 모든 가로등 및 보안등 좌표를 저장합니다.

<img src="https://user-images.githubusercontent.com/51476083/99691761-e47ca100-2acc-11eb-9698-edd7d5800dec.png">

###### 경계 내에 모든 가로등 및 보안등 좌표
<hr>

🔮 이 때, 최단 경로구간에 있는 가로등의 개수를 파악하기 위해 점과 직선사이 거리 공식을 이용합니다. <br>
🔮 골목과 가로등 사이의 거리를 계산한 후 골목에 위치하는 가로등인지 파악합니다. <br>
🔮 또한 골목 내 가로등의 개수를 파악합니다.

<img src="https://user-images.githubusercontent.com/51476083/99692492-b6e42780-2acd-11eb-985d-39ffc127129d.png">

###### 가로등 개수 파악 예시
<br>

<img src="https://user-images.githubusercontent.com/51476083/99692129-4b9a5580-2acd-11eb-809b-d24d7e690d23.png">

###### 점과 직선사이 거리 공식
<hr>

🔮 공식을 이용해 최단경로 상의 가로등과 보안등 좌표를 저장합니다.

<img src="https://user-images.githubusercontent.com/51476083/99693078-53a6c500-2ace-11eb-97fc-3799eb5d3e82.png">

###### 경로 상 가로등과 보안등 좌표 저장
<hr>

🔮 야간 가로등 불빛의 가시거리, 약 73m 를 고려하여 구간을 나눕니다. <br>
🔮 구간 내에 가로등 및 보안등의 개수를 세어서 2개 미만일 경우 우회하는 경로를 추천해줍니다.

<img src="https://user-images.githubusercontent.com/51476083/99693411-ada78a80-2ace-11eb-9c23-138bd890f4f4.png">

###### 기존 경로 내에 안전구간 및 우회 추천구간
<hr>

🔮 경로 상 가로등이 없는 구간을 발견하면, 그 구간의 길이를 반지름으로 하는 원을 기준으로 주변 가로등 및 보안등을 확인합니다. <br>
🔮 단, 탐색 시 반경 내에 기존 경로 상의 가로등 좌표와 중복되는 가로등은 제거해줍니다. <br>
🔮 반경 내에 가로등이 없을 경우 기존의 경로를 추천해줍니다.

<img src="https://user-images.githubusercontent.com/51476083/99694348-acc32880-2acf-11eb-98c2-49b12ca3ccce.png">

###### 주변 가로등 재탐색
<hr>

🔮 목적지와 가까워지는 거리에 우회하기 좋은 가로등 또는 보안등을 선별합니다.

<img src="https://user-images.githubusercontent.com/51476083/99694825-2a873400-2ad0-11eb-9c1b-7fe29aba2cc7.png">

###### 우회 가로등 선별 
<hr>

🔮 선별한 가로등으로 우회하여 기존 목적지까지 최단경로를 재탐색합니다.

<img src="https://user-images.githubusercontent.com/51476083/99695041-6e7a3900-2ad0-11eb-9288-92a6c2407fa2.png">

###### 우회경로 재탐색

<hr>
