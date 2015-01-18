var VicEm = VicEm || {};

VicEm.Settings = function() {

    this.filters = {
        nswSa : false,
        safe : true,
        falseAlarms : true
    };

    var savedFilters = localStorage.getItem( 'settings.filters' );
    if ( savedFilters !== "undefined" ) {
        this.filters = JSON.parse( savedFilters );
    }

};

VicEm.Settings.prototype.save = function() {
    localStorage.setItem( 'settings.filters', JSON.stringify( this.filters ) );
};

VicEm.Location = function( source ) {

    this.name = source.name;

    this.latitude = parseFloat( source.latitude );
    this.localGovernmentArea = source.localGovernmentArea;
    this.locationInfo = source.locationInfo;
    this.longitude = parseFloat( source.longitude );
    this.stateGovernmentRegion = source.stateGovernmentRegion;
    this.type = source.type;

};

VicEm.Location.prototype.getNameFull = function() {
    if ( this.locationInfo ) {
        return this.locationInfo + ", " + this.name;
    } else {
        return this.name;
    }
};

/**
 * Arbitrary measurement of distance between two locations.
 * Not kms, not anything specific - it is the calculation of:
 *
 *   sqrt( delta lat ^ 2 + delta y ^ 2 )
 */
VicEm.Location.prototype.getDistanceTo = function( location ) {
    var x = location.latitude  - this.latitude;
    var y = location.longitude -  this.longitude;
    return Math.sqrt( x * x + y * y );
};

VicEm.Location.prototype.getMidPoint = function( location ) {
    var x = location.latitude  - this.latitude;
    var y = location.longitude -  this.longitude;
    return new VicEm.Location({
        latitude: location.latitude - x / 2,
        longitude: location.longitude - y / 2
    });
};

VicEm.Location.UNKNOWN = new VicEm.Location({
    name: "Unknown",
    latitude: "-37.8136",
    longitude: "144.9631"
});

VicEm.Location.MELBOURNE = new VicEm.Location({
    name: "Melbourne",
    latitude: "-37.8136",
    longitude: "144.9631"
});

VicEm.Event = function( data ) {

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
        this.locations.push( new VicEm.Location( data.geo.points[ i ] ) );
    }

};

VicEm.Event.prototype.getIcon = function() {
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

VicEm.Event.prototype.isSafe = function() {
    return this.status === "Safe" || this.status === "Complete" || this.status === "Patrolled";
};

VicEm.Event.prototype.getStatusColour = function() {
    if ( this.isSafe() ) {
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

VicEm.Event.prototype.getLocation = function() {
    if ( this.locations.length == 0 ) {
        return VicEm.Location.UNKNOWN;
    } else {
        return this.locations[ 0 ];
    }
};

VicEm.Event.prototype.getName = function() {
    return this.name;
};

VicEm.Event.prototype.getAllLocationsString = function() {
    if ( this.locations.length == 0 ) {
        return VicEm.Location.UNKNOWN.name;
    } else {
        var names = [];
        for ( var i = 0; i < this.locations.length; i ++ ) {
            names.push( this.locations[ i ].name );
        }
        return names.join( ', ' );
    }
};

VicEm.Event.prototype.getTimeString = function( timestamp ) {
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

VicEm.Event.prototype.getLastUpdatedString = function() {
    return this.getTimeString( this.updatedTime );
};

VicEm.Event.prototype.getCreatedString = function() {
    return this.getTimeString( this.createdTime );
};

VicEm.Event.Incident = function( data ) {

    VicEm.Event.call( this, data );

    this.eventId = data.eventId;
    this.title = data.title;
    this.size = data.size;
    this.sizeHa = data.sizeHa;
    this.resourceCount = data.resourceCount;
    this.agencyArea = data.agencyArea;
    this.timeWentSafe = data.timeWentSafe;
    this.agencyId = data.agencyId;

};

VicEm.Event.Incident.prototype = Object.create( VicEm.Event.prototype );
VicEm.Event.Incident.prototype.constructor = Object.create( VicEm.Event.Incident );

VicEm.Event.prototype.getName = function() {
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

VicEm.Event.Warning = function( data ) {

    VicEm.Event.call( this, data );

    this.eventId = data.eventId;
    this.layer = data.layer;
    this.agencyUrl = data.agencyUrl;

};

VicEm.Event.Warning.prototype = Object.create( VicEm.Event.prototype );
VicEm.Event.Warning.prototype.constructor = Object.create( VicEm.Event.Incident );

VicEm.Event.Warning.prototype.getWarningUrl = function() {
    return "http://www.emergency.vic.gov.au/warnings/" + this.id + "_bare.html"
};

VicEm.Event.Warning.prototype.getAdviceType = function() {
    if ( this.layerGroup == "Storm" ) {
        return "Advice";
    } else {
        return this.layerGroup;
    }
};

VicEm.Event.Warning.prototype.getAgencyShort = function() {
    if ( this.agencyShort == "VICSES" ) {
        return "SES";
    }

    return this.agencyShort;
};

VicEm.Event.Warning.prototype.getIcon = function() {
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
