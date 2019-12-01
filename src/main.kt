import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.table
import kotlinx.html.js.tr
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
    test()
    jsMap()

    drawTable(document.getElementById("table") as HTMLDivElement)
    //js("myFunction();")
}

fun getMockedObject(): List<Weather> = listOf(
        Weather("Warsaw", WeatherDto(), MainDto(), WindDto()),
        Weather("Lodz", WeatherDto(), MainDto(), WindDto()),
        Weather("Gdansk", WeatherDto(), MainDto(), WindDto()),
        Weather("Krakow", WeatherDto(), MainDto(), WindDto())
)

@JsName("dataWoj")
fun test() : List<Weather> {
    return getMockedObject()
}

fun jsMap(){
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
    var exampleData = _.dataWoj();
    
    
     /* 	for (var i = 0; i < data.length; i++) {

						var dataWoj = data[i].woj;


						var areaWoj = parseFloat(data[i].area)
						var populationWoj = parseFloat(data[i].population)
						var densityWoj = parseFloat(data[i].density);


						for (var j = 0; j < json.features.length; j++) {

							var jsonWoj = json.features[j].properties.name;

							if (dataWoj == jsonWoj) {

								json.features[j].properties.area = areaWoj;
								json.features[j].properties.population = populationWoj;
								json.features[j].properties.density = densityWoj;

								break;

							}
						}
					} */



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
				.text(function(d) { return "Area: " + formatNumbers(d.properties.area) + " sq. km"; })
				.append("tspan")
				.attr("x", 700)
				.attr("dy", 25)
				.text(function(d) { return "Population: " + formatNumbers(d.properties.population); })
				.append("tspan")
				.attr("x", 700)
				.attr("dy", 25)
				.text(function(d) { return "Population density: " + d.properties.density; });


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