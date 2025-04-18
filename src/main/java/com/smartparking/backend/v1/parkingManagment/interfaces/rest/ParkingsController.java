package com.smartparking.backend.v1.parkingManagment.interfaces.rest;

import com.smartparking.backend.v1.parkingManagment.domain.model.aggregates.Parking;
import com.smartparking.backend.v1.parkingManagment.domain.model.entities.ParkingSpot;
import com.smartparking.backend.v1.parkingManagment.domain.model.queries.GetParkingByIdQuery;
import com.smartparking.backend.v1.parkingManagment.domain.model.queries.GetParkingSpotsByParkingIdQuery;
import com.smartparking.backend.v1.parkingManagment.domain.services.ParkingCommandService;
import com.smartparking.backend.v1.parkingManagment.domain.services.ParkingQueryService;
import com.smartparking.backend.v1.parkingManagment.interfaces.rest.resources.AddParkingSpotResource;
import com.smartparking.backend.v1.parkingManagment.interfaces.rest.resources.CreateParkingResource;
import com.smartparking.backend.v1.parkingManagment.interfaces.rest.resources.ParkingResource;
import com.smartparking.backend.v1.parkingManagment.interfaces.rest.resources.ParkingSpotResource;
import com.smartparking.backend.v1.parkingManagment.interfaces.rest.transform.AddParkingSpotCommandFromResourceAssembler;
import com.smartparking.backend.v1.parkingManagment.interfaces.rest.transform.CreateParkingCommandFromResourceAssembler;
import com.smartparking.backend.v1.parkingManagment.interfaces.rest.transform.ParkingResourceFromEntityAssembler;
import com.smartparking.backend.v1.parkingManagment.interfaces.rest.transform.ParkingSpotResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/api/v1/parkings", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Parking", description = "Parking management")
public class ParkingsController {
    private final ParkingCommandService parkingCommandService;
    private final ParkingQueryService parkingQueryService;

    public ParkingsController(ParkingCommandService parkingCommandService, ParkingQueryService parkingQueryService) {
        this.parkingCommandService = parkingCommandService;
        this.parkingQueryService = parkingQueryService;
    }

    @Operation(summary = "Create a new parking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<ParkingResource> createParking(@RequestBody CreateParkingResource resource) {
        Optional<Parking> parking = this.parkingCommandService
                .handle(CreateParkingCommandFromResourceAssembler.toCommandFromResource(resource));

        return parking.map(source->
                        new ResponseEntity<>(ParkingResourceFromEntityAssembler.toResourceFromEntity(source), CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Add a parking spot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking spot added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/{parkingId}/spots")
    public ResponseEntity<ParkingSpotResource> addParkingSpot(@PathVariable Long parkingId,
                                                              @RequestBody AddParkingSpotResource resource) {
        Optional<ParkingSpot> parkingSpot = this.parkingCommandService
                .handle(AddParkingSpotCommandFromResourceAssembler.toCommandFromResource(resource, parkingId));

        return parkingSpot.map(source ->
                        new ResponseEntity<>(ParkingSpotResourceFromEntityAssembler.toResourceFromEntity(source), CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Get all parking spots by parking id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parkings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parking not found")
    })
    @GetMapping("/{parkingId}/spots")
    public ResponseEntity<List<ParkingSpotResource>> getParkingSpotsByParkingId(@PathVariable Long parkingId) {
        var query = new GetParkingSpotsByParkingIdQuery(parkingId);
        List<ParkingSpot> spots = this.parkingQueryService.handle(query);

        if (spots.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ParkingSpotResource> parkingSpotResources = spots.stream()
                .map(ParkingSpotResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return new ResponseEntity<>(parkingSpotResources, CREATED);
    }

    @Operation(summary = "Get parking by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parking not found")
    })
    @GetMapping("/{parkingId}")
    public ResponseEntity<ParkingResource> getParkingById(@PathVariable Long parkingId) {
        var query = new GetParkingByIdQuery(parkingId);
        Optional<Parking> parking = this.parkingQueryService.handle(query);

        return parking.map(source ->
                        new ResponseEntity<>(ParkingResourceFromEntityAssembler.toResourceFromEntity(source), CREATED))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
