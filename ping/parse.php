<?php

class Database {

	const EVENT = "event";
	const WARNING = "warn";
	const INCIDENT = "incident";
	const GEO_POINT = "geoPoint";
	private $pdo;

	public function __construct() {
		$dir = dirname( $_SERVER[ 'SCRIPT_FILENAME' ] );
		$dbFile = "$dir/feed.db";
		if ( file_exists( $dbFile ) ) {
			unlink( $dbFile );
		}

		$this->pdo = new PDO( "sqlite:$dbFile" );
		$this->createEventTable();
		$this->createIncidentTable();
		$this->createWarningTable();
		$this->createGeoPointTable();
	}

	private function createWarningTable() {
		$table = self::WARNING;
		$this->pdo->exec( "
CREATE TABLE $table (
	eventId INTEGER,
	layer TEXT,
	agencyUrl TEXT
)
");
	}

	private function createIncidentTable() {
		$table = self::INCIDENT;
		$this->pdo->exec( "
CREATE TABLE $table (
	eventId INTEGER,
	title TEXT,
	size TEXT,
	sizeHa TEXT,
	resourceCount INTEGER,
	agencyArea TEXT,
	timeWentSafe TEXT,
	agencyId TEXT
);");
	}

	private function createEventTable() {
		$table = self::EVENT;
		$this->pdo->exec( "
CREATE TABLE $table (
	id TEXT,
	type TEXT,
	layerGroup TEXT,
	name TEXT,
	status TEXT,
	icon TEXT,
	state TEXT,
	agencyShort TEXT,
	agency TEXT,
	createdTime INTEGER,
	updatedTime INTEGER,
	htmlFurther TEXT,
	severity INTEGER,
	timeSeverity INTEGER,
	color TEXT
);");
	}

	private function createGeoPointTable() {
		$table = self::GEO_POINT;
		$this->pdo->exec( "
CREATE TABLE $table (
	name TEXT,
	type TEXT,
	locationInfo TEXT,
	latitude NUMERIC,
	longitude NUMERIC,
	localGovernmentArea TEXT,
	localGovernmentRegion TEXT
);");
	}

	public function insertIncident( $eventId, stdClass $incident ) {
		$table = self::INCIDENT;
		$statement = $this->pdo->prepare("
			INSERT INTO $table (
				eventId,
				title,
				size,
				sizeHa,
				resourceCount,
				agencyArea,
				timeWentSafe,
				agencyId
			) VALUES (
				:eventId,
				:title,
				:size,
				:sizeHa,
				:resourceCount,
				:agencyArea,
				:timeWentSafe,
				:agencyId
			)
		");

		$statement->execute( array(
			'eventId' => $eventId,
			'title' => $incident->title,
			'size' =>  $incident->size,
			'sizeHa' => $incident->sizeHa,
			'resourceCount' =>  $incident->resourceCount,
			'agencyArea' => $incident->agencyArea,
			'timeWentSafe' =>  $incident->timeWentSafe,
			'agencyId' => $incident->agencyId,
		));

		return $this->pdo->lastInsertId();
	}

	public function insertWarning( $eventId, stdClass $warning ) {
		$table = self::WARNING;
		$statement = $this->pdo->prepare("
			INSERT INTO $table (
				eventId,
				layer,
				agencyUrl
			) VALUES (
				:eventId,
				:layer,
				:agencyUrl
			)
		");

		$statement->execute( array(
			'eventId' => $eventId,
			'layer' => $warning->layer,
			'agencyUrl' => $warning->agencyUrl,
		));

		return $this->pdo->lastInsertId();
	}

	public function insertEvent( stdClass $event ) {
		$table = self::EVENT;
		$statement = $this->pdo->prepare("
			INSERT INTO $table (
				id,
				type,
				layerGroup,
				name,
				status,
				icon,
				state,
				agencyShort,
				agency,
				createdTime,
				updatedTime,
				htmlFurther,
				severity,
				timeSeverity,
				color
			) VALUES (
				:id,
				:type,
				:layerGroup,
				:name,
				:status,
				:icon,
				:state,
				:agencyShort,
				:agency,
				:createdTime,
				:updatedTime,
				:htmlFurther,
				:severity,
				:timeSeverity,
				:color
			)
		");

		$params = array(
			'id' => $event->id,
			'type' => $event->type,
			'layerGroup' => $event->layerGroup,
			'name' => $event->name,
			'status' => $event->status,
			'icon' => $event->icon,
			'state' => $event->state,
			'agencyShort' => $event->agencyShort,
			'agency' => $event->agency,
			'createdTime' => $event->createdTime,
			'updatedTime' => $event->updatedTime,
			'htmlFurther' => $event->htmlFurther,
			'severity' => $event->severity,
			'timeSeverity' => $event->timeSeverity,
			'color' => $event->color,
		);

		$statement->execute( $params );
		return $this->pdo->lastInsertId();
	}

	private function insertGeoPoint( stdClass $point ) {

	}

}

$db = new Database();
$dir = dirname( $_SERVER[ 'SCRIPT_FILENAME' ] );

function isIncident( stdClass $event ) {
	foreach( $event as $key => $value ) {
		if ( $key == "title" ) {
			return true;
		}
	}
	return false;
}

foreach ( new DirectoryIterator( $dir . '/data/' ) as $file ) {

	if ( substr( $file->getBasename(), 0, strlen( 'feed_' ) ) != 'feed_' ) {
		continue;
	}

	$feed = json_decode( file_get_contents( $file->getPathname() ) );

	// print_r( $feed );

	if ( !is_object( $feed ) ) {
		continue;
	}

	/** @var stdClass $event */
	foreach( $feed->events as $event ) {

		$eventId = $db->insertEvent( $event );

		foreach ( $event->geo->points as $point ) {

		};

		if ( isIncident( $event ) ) {
			$db->insertIncident( $eventId, $event );
		} else {
			$db->insertWarning( $eventId, $event );
		}
	}
}
