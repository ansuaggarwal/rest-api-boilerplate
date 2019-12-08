<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no">
<meta charset="utf-8">
<title>YoursRide</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

<style>
#map {
	height: 65%;
}

html, body {
	height: 100%;
	margin: 0;
	padding: 0;
}

#allocationtable {
	height: 35%;
}

table {
	border-collapse: collapse;
	width: 100%;
}

th, td {
	padding: 8px;
	text-align: center;
	border-bottom: 1px solid #ddd;
}
</style>
</head>
<body>

	<div id="map"></div>
	<div id="allocationtable"></div>

	<script>
		$(document)
				.ready(
						function() {
							$
									.ajax({
										url : "/yoursride/allocationdetails",
										dataType : 'jsonp',
										jsonpCallback : 'p',
									})
									.then(
											function(data) {

												text = "<table><tr><td><b>Driver Name</b></td><td><b>Customer Name</b></td><td><b>Status</b></td></tr>"

												for (i = 0; i < data.length; i++) {
													text += "<tr><td>"
															+ data[i].DriverName
															+ "</td><td>"
															+ data[i].CustomerName
															+ "</td><td>"
															+ data[i].Status
															+ "</td>";
												}
												text += "</table>";
												$('#allocationtable').empty();
												$('#allocationtable').append(
														text);

											});
							alert("Click anywhere inside the map to choose customer pickup location");
						});

		function initMap() {

			$
					.ajax({
						url : "/yoursride/driver",
						dataType : 'jsonp',
						jsonpCallback : 'p',
					})
					.then(
							function(data) {
								var melbourne = {
									lat : -37.7514,
									lng : 145.137
								};
								var map = new google.maps.Map(document
										.getElementById('map'), {
									zoom : 13,
									center : melbourne
								});

								var labels = 'ABCDE';
								var locations = [];
								for (i = 0; i < data.length; i++) {
									locations.push(new google.maps.LatLng(
											data[i].lat, data[i].longitude));
								}

								var markers = locations.map(function(location,
										i) {
									return new google.maps.Marker({
										position : location,
										label : labels[i]
									});
								});

								// Add a marker clusterer to manage the markers.
								var markerCluster = new MarkerClusterer(
										map,
										markers,
										{
											imagePath : 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
										});

								// This event listener calls addMarker() when the map is clicked.
								google.maps.event
										.addListener(
												map,
												'click',
												function(event) {
													var myLatLng = event.latLng;
													var lat = myLatLng.lat();
													var lng = myLatLng.lng();

													var nm = prompt("Enter Your Name");
													var obj = new Object();
													obj.name = nm;
													obj.lat = lat;
													obj.longitude = lng;

													var myKeyVals = JSON
															.stringify(obj);

													var saveData = $
															.ajax({
																type : 'POST',
																url : "/yoursride/customer/create",
																data : myKeyVals,
																dataType : "text",
																success : function(
																		resultData) {

																	if (resultData == '')
																		alert("You are in waiting Queue");
																	else {
																		var parsedJson = JSON
																				.parse(resultData);
																		alert("Driver Allocated to customer is : "
																				+ parsedJson.name);

																		var coordinates = [];

																		coordinates
																				.push(new google.maps.LatLng(
																						lat,
																						lng));
																		coordinates
																				.push(new google.maps.LatLng(
																						parsedJson.lat,
																						parsedJson.longitude));

																		var line = new google.maps.Polyline(
																				{
																					path : coordinates,
																					strokeColor : "#FF0000",
																					strokeOpacity : 1.0,
																					strokeWeight : 10,
																					map : map
																				});
																	}
																	
																	$
																	.ajax(
																			{
																				url : "/yoursride/allocationdetails",
																				dataType : 'jsonp',
																				jsonpCallback : 'p',
																			})
																	.then(
																			function(
																					data) {

																				text = "<table><tr><td><b>Driver Name</b></td><td><b>Customer Name</b></td><td><b>Status</b></td></tr>"

																				for (i = 0; i < data.length; i++) {
																					text += "<tr><td>"
																							+ data[i].DriverName
																							+ "</td><td>"
																							+ data[i].CustomerName
																							+ "</td><td>"
																							+ data[i].Status
																							+ "</td>";
																				}
																				text += "</table>";
																				$(
																						'#allocationtable')
																						.empty();
																				$(
																						'#allocationtable')
																						.append(
																								text);
																			});
																}
															});
													saveData
															.error(function() {
																alert("Something went wrong");
															});

													addMarker(event.latLng, map);

												});
							});
		}

		// Adds a marker to the map.

		var labels = 'FGHIJKLMNOPQRSTUVWXYZ';
		var labelIndex = 0;

		function addMarker(location, map) {
			// Add the marker at the clicked location, and add the next-available label
			// from the array of alphabetical characters.
			var marker = new google.maps.Marker({
				position : location,
				label : labels[labelIndex++ % labels.length],
				map : map
			});
		}
	</script>
	<script
		src="https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/markerclusterer.js">
		
	</script>
	<script async defer
		src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAIGrqLskWmDVu2wmBuhNXW6hADUq8gEs0&callback=initMap">
		
	</script>
</body>
</html>