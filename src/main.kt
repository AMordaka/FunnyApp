import kotlinx.html.dom.create
import kotlinx.html.js.table
import kotlinx.html.js.tr
import kotlinx.html.td
import kotlinx.html.thead
import kotlinx.html.tr
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document

fun drawTable(tableDiv: HTMLDivElement) {
    val province = getMockedObject()

    val table = document.create.table {
        thead {
            tr {
                td { +"Województwo" }
                td { +"Temperatura" }
                td { +"Ciśnienie" }
                td { +"Wilgotność" }
                td { +"Temp min" }
                td { +"Temp max" }
            }
        }
    }

    province.forEach {
        table.appendChild(document.create.tr {
            td { +it.name }
            td { +it.main.temp.toString() }
            td { +it.main.pressure.toString() }
            td { +it.main.humidity.toString() }
            td { +it.main.temp_min.toString() }
            td { +it.main.temp_max.toString() }
        })
    }
    tableDiv.appendChild(table)
}

fun main(args: Array<String>) {

/*    window.fetch("https://openweathermap.org/data/2.5/weather?q=London&appid=b905529402432c359119e9bd9bdea823").then { res ->
        res.json().then { println(res)}
    }*/
    getData()
    jsMap()
    drawTable(document.getElementById("table") as HTMLDivElement)
}

fun getMockedObject(): List<Weather> = listOf(
        Weather("Łódzkie", WeatherDto(), MainDto(15.0, 500.0, 300.0, 20.0, 30.0), WindDto()),
        Weather("Mazowieckie", WeatherDto(), MainDto(), WindDto()),
        Weather("Lubelskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Lubuskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Zachodniopomorskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Pomorskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Warmińsko-mazurskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Podlaskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Kujawsko-pomorskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Wielkopolskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Dolnośląskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Opolskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Śląskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Małopolskie", WeatherDto(), MainDto(), WindDto()),
        Weather("Podkarpackie", WeatherDto(), MainDto(), WindDto()),
        Weather("Świętokrzyskie", WeatherDto(), MainDto(), WindDto())


)

//TODO should invoke rest api
@JsName("dataWoj")
fun getData(): String {
    return JSON.stringify(getMockedObject())
}

fun jsMap() {
    js("""
	d3.select("div", "#wrapper")
				.append("div")
				.attr("id", "content");

	var margin = {top: 5, right: 25, bottom: 5, left: 5};

	var width = 900 - margin.left - margin.right,
    	height = 750 - margin.top - margin.bottom;


    var projection = d3.geo.mercator()
					.center([20, 51.75])
					.translate([ width/2, height/2 ])
					.scale(3700);


	var path = d3.geo.path()
					.projection(projection);


	var svg = d3.select("#content")
				.append("svg")
				.attr("width", width + margin.left + margin.right)
    			.attr("height", height + margin.top + margin.bottom)
  				.append("g")
    			.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var formatNumbers =  d3.format(",");


    d3.json("https://gist.githubusercontent.com/mbaba/78e2c2295c632a1f0985/raw/659ac0cf59157f94916412380ea1952f8aaf99be/woj_maps.json", function(json) {
        var mockData = JSON.parse(_.dataWoj());
      	for (var i = 0; i < mockData.length; i++) {

            var dataWoj = mockData[i].name;
    
            var tempWoj = parseFloat(mockData[i].main.temp);
            var pressureWoj = parseFloat(mockData[i].main.pressure);
            var humidityWoj = parseFloat(mockData[i].main.humidity);

						for (var j = 0; j < json.features.length; j++) {

							var jsonWoj = json.features[j].properties.name;

							if (dataWoj == jsonWoj) {
								json.features[j].properties.area = tempWoj;
								json.features[j].properties.population = pressureWoj;
								json.features[j].properties.density = humidityWoj;
								break;
							}
						}
					} 



    	var mapWoj = svg.selectAll("g")
                    .data(json.features)
                    .enter()
                    .append("g")

		mapWoj.append("path")
				   .attr("d", path)
				   .attr("class", "path");



	 	mapWoj.append("text")
				.attr("x", 700)
				.attr("y", 100)
				.attr("text-anchor", "start")
				.attr("fill", "none")
				.text(function(d) { return d.properties.name; })
				.append("tspan")
				.attr("x", 700)
				.attr("dy", 25)
				.text(function(d) { return "Temp: " + formatNumbers(d.properties.area) + " C"; })
				.append("tspan")
				.attr("x", 700)
				.attr("dy", 25)
				.text(function(d) { return "Ciśnienie: " + formatNumbers(d.properties.population); + " mPa" })
				.append("tspan")
				.attr("x", 700)
				.attr("dy", 25)
				.text(function(d) { return "Wilgotność: " + d.properties.density; });


		mapWoj.on("mouseover", function() {
                    d3.select(this)
                    .classed("hover", true)
                });

        mapWoj.on("mouseout", function() {
            	d3.select(this)
                .classed("hover", false)
        		});


	});

    """)
}