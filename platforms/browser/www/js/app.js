(function () {
    'use strict';
    var module = angular.module('app', ['onsen', 'leaflet-directive'])
        .config(function($sceDelegateProvider) {
            $sceDelegateProvider.resourceUrlWhitelist([
                // Allow same origin resource loads.
                'self',
                // Allow loading from our assets domain.  Notice the difference between * and **.
                'http://www.emergency.vic.gov.au/**'
            ]);
        });

    var Location = function( source ) {

        this.name = source.name;

        this.latitude = parseFloat( source.latitude );
        this.localGovernmentArea = source.localGovernmentArea;
        this.locationInfo = source.locationInfo;
        this.longitude = parseFloat( source.longitude );
        this.stateGovernmentRegion = source.stateGovernmentRegion;
        this.type = source.type;

    };

    Location.prototype.getNameFull = function() {
        if ( this.locationInfo ) {
            return this.locationInfo + ", " + this.name;
        } else {
            return this.name;
        }
    };

    Location.UNKNOWN = new Location({
        name: "Unknown"
    });

    var Event = function( data ) {

        this.id = data.id;
        this.type = data.type;
        this.layerGroup = data.layerGroup;
        this.name = data.name;
        this.status = data.status;
        this.icon = data.icon;
        this.state = data.state;
        this.agencyShort = data.agencyShort;
        this.agency = data.agency;
        this.createdTime = data.createdTime;
        this.updatedTime = data.updatedTime;
        this.htmlFurther = data.htmlFurther;
        this.severity = data.severity;
        this.timeSeverity = data.timeSeverity;
        this.color = data.color;

        this.locations   = [];

        for ( var i = 0; i < data.geo.points.length; i ++ ) {
            this.locations.push( new Location( data.geo.points[ i ] ) );
        }

    };

    Event.prototype.getIcon = function() {
        if ( this.layerGroup === "Medical" ) {
            return "icon-medical-emergency";
        } else if ( this.name === "Road Accident" ) {
            return "icon-car";
        } else if ( this.name === "Tree Down Traffic Haz" ) {
            return "icon-tree-car";
        } else if ( this.name === "Ses Incident Other" ) {
            return "icon-ses";
        } else if ( this.name === "Hazardous Material" ) {
            return "icon-hazchem";
        } else if ( this.name === "Building Damage" ) {
            return "icon-building";
        } else if ( this.name === "Animal Incident" ) {
            return "icon-animal";
        } else if ( this.name === "Full Call" ) {
            // I took the meaning of "Full Call" == "Fire Alarm" from https://www.facebook.com/cfavic/posts/10151542873654416
            return "icon-fire-alarm";
        } else if ( this.name === "Building Fire" ) {
            return "icon-building-fire";
        } else if ( this.name === "False Alarm" ) {
            if ( this.layerGroup === "Fire" ) {
                return "icon-fire-false-alarm";
            }
        } else if ( this.name === "Bushfire" ) {
            return "icon-tree-fire";
        } else if ( this.name === "Washaway" ) {
            return "icon-rescue";
        } else if ( this.name === "Planned Burn" ) {
            return "icon-fire-planned";
        } else if ( this.name === "Rescue" ) {
            return "icon-rescue";
        } else if ( this.name === "Assist - Ambulance Vic" ) {
            return "icon-medical-emergency";
        } else if ( this.name === "Building" ) {
            if ( this.layerGroup === "Fire" ) {
                return "icon-building-fire";
            }
        } else if ( this.name === "Incident" ) {
            if ( this.layerGroup === "Accident / Rescue" ) {
                return "icon-car";
            } else {
                return "";
            }
        } else {
            if ( this.layerGroup === "Fire" ) {
                return "icon-fire";
            } else if ( this.layerGroup === "Tree Down" ) {
                return "icon-tree";
            }
        }

        if ( this.name === "Other" && this.layerGroup === "Other" ) {
            return "icon-other";
        }
    };

    Event.prototype.getStatusColour = function() {
        if ( this.status == "Safe"
            || this.status == "Complete"
            || this.status == "Patrolled" ) {
            return "#efe";
        } else if ( this.status == "Under Control" ) {
            return "#eef";
        } else if ( this.status == "Request For Assistance"
            || this.status == "Responding" ) {
            return "#ffe";
        } else if ( this.status == "Not Yet Under Control" ) {
            return "#fee";
        } else if ( this.status == "Incident"
            || this.status == "Unknown"
            || this.status == "" ) {
            return "#fff";
        }

        return "#eee";
    };

    Event.prototype.getLocation = function() {
        if ( this.locations.length == 0 ) {
            return Location.UNKNOWN;
        } else {
            return this.locations[ 0 ];
        }
    };

    Event.prototype.getName = function() {
        return this.name;
    };

    Event.prototype.getAllLocationsString = function() {
        if ( this.locations.length == 0 ) {
            return Location.UNKNOWN.name;
        } else {
            var names = [];
            for ( var i = 0; i < this.locations.length; i ++ ) {
                names.push( this.locations[ i ].name );
            }
            return names.join( ', ' );
        }
    };

    Event.prototype.getTimeString = function( timestamp ) {
        var now = new Date().getTime();
        var diff = now - timestamp;

        diff = Math.floor( diff / ( 1000 * 60 ) ); // milliseconds

        var mins      = diff % 60;
        var minPlural = mins == 1 ? "" : "s";
        var minString = mins + " min" + minPlural;

        if (diff < 60) {
            return minString
        } else {
            var hours = Math.floor( diff / 60 );
            var hourPlural = hours == 1 ? "" : "s";
            var hourString = hours + "hr" + hourPlural;

            if (mins == 0) {
                return hourString;
            } else {
                return hourString + " " + minString;
            }
        }
    };

    Event.prototype.getLastUpdatedString = function() {
        return this.getTimeString( this.updatedTime );
    };

    Event.prototype.getCreatedString = function() {
        return this.getTimeString( this.createdTime );
    };

    Event.Incident = function( data ) {

        Event.call( this, data );

        this.eventId = data.eventId;
        this.title = data.title;
        this.size = data.size;
        this.sizeHa = data.sizeHa;
        this.resourceCount = data.resourceCount;
        this.agencyArea = data.agencyArea;
        this.timeWentSafe = data.timeWentSafe;
        this.agencyId = data.agencyId;

    };

    Event.Incident.prototype = Object.create( Event.prototype );
    Event.Incident.prototype.constructor = Object.create( Event.Incident );

    Event.prototype.getName = function() {
        if ( this.name === "Building" ) {
            return this.name + " " + this.layerGroup;
        } else if ( this.name === "Incident" ) {
            if ( this.layerGroup === "Accident / Rescue" ) {
                return this.layerGroup;
            } else {
                return this.name;
            }
        } else if ( this.name === "Ses Incident Other" ) {
            return "SES Incident";
        } else if ( this.name === "Full Call" ) {
            // I took the meaning of "Full Call" == "Fire Alarm" from https://www.facebook.com/cfavic/posts/10151542873654416
            return "Fire Alarm (Full Call)";
        } else if ( this.name === "False Alarm" ) {
            return this.layerGroup + " (False Alarm)";
        } else if ( this.name === "Assist - Ambulance Vic" ) {
            return "Medical (with " + this.agencyShort + " assisting)";
        } else if ( this.name === "Washaway" ) {
            return "Rescue (Washaway)";
        } else if ( this.name === "Other" ) {
            if ( this.layerGroup === "Fire" ) {
                return "Fire";
            } else {
                return "Other";
            }
        }

        return this.name;
    };

    Event.Warning = function( data ) {

        Event.call( this, data );

        this.eventId = data.eventId;
        this.layer = data.layer;
        this.agencyUrl = data.agencyUrl;

    };

    Event.Warning.prototype = Object.create( Event.prototype );
    Event.Warning.prototype.constructor = Object.create( Event.Incident );

    Event.Warning.prototype.getWarningUrl = function() {
        return "http://www.emergency.vic.gov.au/warnings/" + this.id + "_bare.html"
    };

    Event.Warning.prototype.getAdviceType = function() {
        if ( this.layerGroup == "Storm" ) {
            return "Advice";
        } else {
            return this.layerGroup;
        }
    };

    Event.Warning.prototype.getAgencyShort = function() {
        if ( this.agencyShort == "VICSES" ) {
            return "SES";
        }

        return this.agencyShort;
    };

    Event.Warning.prototype.getIcon = function() {
        var type = this.getAdviceType();
        if ( type === "Advice" ) {
            return "icon-advice";
        } else if ( type === "Warning" ) {
            return "icon-warning";
        } else if ( type === "Evacuate" ) {
            return "icon-evacuate";
        }

        return "icon-info";
    };

    module.controller('AppController', function ($scope, $http) {

        var loadIncidents = function() {

            $scope.incidents = null;
            $scope.warnings = null;
            $scope.errorLoading = false;

            var feedLink = 'http://emergency.vic.gov.au/feed/feed.json';
            // var feedLink = 'file:///home/pete/code/vic-emergencies/www/js/feed.json';
            $http.get( feedLink ).success(function (data) {

                var warnings = [];
                var incidents = [];
                for (var i = 0; i < data.events.length; i++) {
                    var item = data.events[i];
                    if ( item.hasOwnProperty( 'title' ) ) {
                        incidents.push( new Event.Incident( item ) );
                    } else {
                        warnings.push( new Event.Warning( item ) );
                    }
                }
                $scope.incidents = incidents;
                $scope.warnings = warnings;

            }).error(function (data) {
                $scope.errorLoading = true;
            });
        };

        $scope.refresh = function() {
            loadIncidents();
        };

        loadIncidents();

    });

    module.controller('DetailController', function ($scope, $data, $http) {
        $scope.item = $data.selectedItem;
        console.log( $scope.item );

        $scope.tiles = {
            url: "http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            options: {
                attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }
        };

        var loc = $scope.item.getLocation();

        $scope.mapLocation = {
            lat: loc.latitude,
            lng: loc.longitude,
            zoom: 6
        };

        $scope.markers = {
            incident : {
                icon : {
                    type: 'div',
                    iconSize: [ 46, 46 ],
                    className: 'marker ' + $scope.item.getIcon()
                },
                lat: loc.latitude,
                lng: loc.longitude
            }
        };

        /*
        $http.get( "maps/vic.geojson" ).success( function( data, status ) {

            angular.extend($scope, {
                geojson: {
                    data: data,
                    clickable: false,
                    style: function( feature ) {
                        return {
                            color: "#F33",
                            weight: 3,
                            fillColor: "#FFF",
                            fillOpacity: 0.0
                        }
                    }
                }
            });
        });*/

    });

    module.controller('WarningController', function ($scope, $data) {
        $scope.item = $data.selectedItem;
        console.log( $scope.item );
    });

    module.controller('MasterController', function ($scope, $data) {
        $scope.showWarningDetail = function (item) {
            var selectedItem = item;
            $data.selectedItem = selectedItem;
            $scope.ons.navigator.pushPage('warning.html', {title: selectedItem.title});
        };
        $scope.showIncidentDetail = function (item) {
            var selectedItem = item;
            $data.selectedItem = selectedItem;
            $scope.ons.navigator.pushPage('detail.html', {title: selectedItem.title});
        };
    });

    module.factory('$data', function() {
        return {
            selectedItem: null
        };
    });

})();

