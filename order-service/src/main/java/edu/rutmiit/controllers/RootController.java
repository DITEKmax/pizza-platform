package edu.rutmiit.controllers;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class RootController {

    @GetMapping
    public RepresentationModel<?> getRoot() {
        RepresentationModel<?> rootModel = new RepresentationModel<>();
        rootModel.add(
                linkTo(methodOn(MenuController.class).getAllMenuItems(null, null, 0, 20)).withRel("menu"),
                linkTo(methodOn(OrderController.class).getAllOrders(null, null, 0, 20)).withRel("orders")
        );
        return rootModel;
    }
}
