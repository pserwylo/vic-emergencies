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

    var loadSettings = function() {

        var saved = localStorage.getItem( 'settings' );

        if ( !!saved ) {
            return JSON.parse( saved );
        }

        return {
            filter: {
                nswSa: false,
                falseAlarms: true,
                safe: true
            }
        }
    };

    var settings = loadSettings();

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

            return settings.filter.nswSa || !isNswSa();
        };

        $scope.filterIncidents = function( event ) {

            var isNswSa = function() {
                return event.state != "VIC";
            };

            var isFalseAlarm = function() {
                return event.name == "False Alarm";
            };

            if ( !settings.filter.nswSa && isNswSa() ) {
                return false;
            } else if ( !settings.filter.safe && event.isSafe() ) {
                return false;
            } else if ( !settings.filter.falseAlarms && isFalseAlarm() ) {
                return false;
            } else {
                return true;
            }
        };

        loadIncidents();

    });

    module.controller('DetailController', function ($scope, $data) {
        $scope.item = $data.selectedItem;
        console.log( $scope.item );

        $scope.tiles = {
            url: "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            // url: "file:///home/pete/code/vic-emergencies/www/tiles2/{z}/{x}/{y}.png",
            options: {
                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            }
        };

        var loc = $scope.item.getLocation();
        var distance = loc.getDistanceTo( VicEm.Location.MELBOURNE );
        var mid = loc.getMidPoint( VicEm.Location.MELBOURNE );

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

        $scope.mapLocation = {
            lat: mid.latitude,
            lng: mid.longitude,
            zoom: zoom
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

        var saveSettings = function() {
            localStorage.setItem( 'settings', JSON.stringify( settings ) );
        };

        $scope.$watch( 'settings.filter.nswSa', saveSettings);
        $scope.$watch( 'settings.filter.safe', saveSettings);
        $scope.$watch( 'settings.filter.falseAlarms', saveSettings);

    });

    module.controller('WarningController', function ($scope, $data) {
        $scope.item = $data.selectedItem;
        console.log( $scope.item );
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

