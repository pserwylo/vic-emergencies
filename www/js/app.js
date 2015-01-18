var VicEm = VicEm || {};

(function () {
    'use strict';
    var module = angular.module('app', ['onsen', 'leaflet-directive'])
        .config(function($sceDelegateProvider) {
            $sceDelegateProvider.resourceUrlWhitelist([
                // Allow same origin resource loads.
                'self',
                'http://www.emergency.vic.gov.au/**'
            ]);
        });

    var settings = new VicEm.Settings();

    module.controller('AppController', function ($scope, $http) {

        var loadIncidents = function() {

            $scope.incidents = null;
            $scope.warnings = null;
            $scope.errorLoading = false;

            var feedLink = 'http://www.emergency.vic.gov.au/feed/feed.json';
            // var feedLink = 'file:///home/pete/code/vic-emergencies/www/js/feed.json';
            $http.get( feedLink ).success(function (data) {

                var warnings = [];
                var incidents = [];

                for (var i = 0; i < data.events.length; i++) {
                    var item = data.events[i];
                    if ( item.hasOwnProperty( 'title' ) ) {
                        incidents.push( new VicEm.Event.Incident( item ) );
                    } else {
                        warnings.push( new VicEm.Event.Warning( item ) );
                    }
                }

                $scope.incidents = incidents;
                $scope.warnings = warnings;

            }).error(function () {
                $scope.errorLoading = true;
            });
        };

        $scope.refresh = function() {
            loadIncidents();
        };

        $scope.showMenu = function() {
            $scope.ons.navigator.pushPage( 'templates/settings.html' );
        };

        $scope.filterWarnings = function( warning ) {
            var isNswSa = function() {
                return warning.state != "VIC";
            };

            return settings.filters.nswSa || !isNswSa();
        };

        $scope.filterIncidents = function( event ) {

            var isNswSa = function() {
                return event.state != "VIC";
            };

            var isFalseAlarm = function() {
                return event.name == "False Alarm";
            };

            if ( !settings.filters.nswSa && isNswSa() ) {
                return false;
            } else if ( !settings.filters.safe && event.isSafe() ) {
                return false;
            } else if ( !settings.filters.falseAlarms && isFalseAlarm() ) {
                return false;
            } else {
                return true;
            }
        };

        loadIncidents();

    });

    var mapTiles = function() {
        return {
            url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            // url: "file:///home/pete/code/vic-emergencies/www/tiles2/{z}/{x}/{y}.png",
            options: {
                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }
        };
    };

    var mapZoom = function( location ) {
        var distance = location.getDistanceTo( VicEm.Location.MELBOURNE );
        var zoom = 6;
        if ( distance > 1.6 ) {
            zoom = 5;
        } else if ( distance > 1.4 ) {
            zoom = 6;
        } else if ( distance > 1 ) {
            zoom = 7;
        } else {
            zoom = 8;
        }
        return zoom;
    };

    var mapMarker = function( location, icon ) {
        return {
            icon : {
                type: 'div',
                iconSize: [ 46, 46 ],
                className: 'marker ' + icon
            },
            lat: location.latitude,
            lng: location.longitude
        }
    };

    module.controller('DetailController', function ($scope, $data) {
        $scope.item = $data.selectedItem;
        console.log( $scope.item );

        $scope.tiles = mapTiles();

        var loc = $scope.item.getLocation();
        var mid = loc.getMidPoint( VicEm.Location.MELBOURNE );

        $scope.mapLocation = {
            lat: mid.latitude,
            lng: mid.longitude,
            zoom: mapZoom( loc )
        };

        $scope.markers = {
            incident : mapMarker( loc, $scope.item.getIcon() )
        };

        $scope.$on('leafletDirectiveMarker.click', function() {
            $scope.mapLocation = {
                lat: loc.latitude,
                lng: loc.longitude,
                zoom: 14
            }
        });

    });

    module.controller('SettingsController', function ($scope) {

        $scope.settings = settings;

        var save = function() {
            $scope.settings.save();
        };

        $scope.$watch( 'settings.filters.nswSa', save);
        $scope.$watch( 'settings.filters.falseAlarms', save);
        $scope.$watch( 'settings.filters.safe', function() {
            if ( !$scope.settings.filters.safe ) {
                $scope.settings.filters.falseAlarms = false;
            }
            save();
        });

    });

    module.controller('WarningController', function ($scope, $data) {
        
        $scope.item = $data.selectedItem;
        console.log( $scope.item );

        $scope.tiles = mapTiles();

        var loc = $scope.item.getLocation();
        var mid = loc.getMidPoint( VicEm.Location.MELBOURNE );

        $scope.mapLocation = {
            lat: mid.latitude,
            lng: mid.longitude,
            zoom: mapZoom( loc )
        };

        $scope.markers = [];

        var locs = $scope.item.getAllLocations();
        for ( var i = 0; i < locs.length; i ++ ) {
            $scope.markers.push( mapMarker( locs[ i ], $scope.item.getIcon() ) )
        }

        $scope.$on('leafletDirectiveMarker.click', function(event) {
            console.log( event );
            $scope.mapLocation = {
                lat: loc.latitude,
                lng: loc.longitude,
                zoom: 9
            }
        });
    });

    module.controller('MasterController', function ($scope, $data) {
        $scope.showWarningDetail = function (item) {
            var selectedItem = item;
            $data.selectedItem = selectedItem;
            $scope.ons.navigator.pushPage('templates/warning.html', {title: selectedItem.title});
        };
        $scope.showIncidentDetail = function (item) {
            var selectedItem = item;
            $data.selectedItem = selectedItem;
            $scope.ons.navigator.pushPage('templates/detail.html', {title: selectedItem.title});
        };
    });

    module.factory('$data', function() {
        return {
            selectedItem: null
        };
    });

})();

