Проект Мониторинг Качества Воздуха

<h2>Папки</h2>

<h3>models</h3>
Есть 3 модели Indice, Region, Stations.

Region - Города. 
Stations - Районы городов.
Indice - Массив вложенный в Stations.

<h3>remote</h3>
RetrofitClient - подключение к API.

<h3>service</h3>
папка service включает в себя GET запросы по разным URL нашего API.

<h3>ui</h3>
включает в себя 3 папки.

<h4>activities</h4>
MainActivity - главный экран приложения.
IndiceActivity - экран при клике на категории, который показывает выбранную информацию.

<h4>adapters</h4>
GridAdapter - выводит блоки выбранного района в GridView.
StationAdapter - выводит районы выбранного города в RecyclerView.

<h4>viewmodels</h4>
RegionViewModel - запрос на получение данных regions.
StationViewModel - запрос на получение данных station.

В программе мы использовали BottomSheet, GridView, Activity, Retrofit, WebView.

BottomSheet включает в себя 2 layout, чтобы переключаться между списком из районов и выбранного района.
