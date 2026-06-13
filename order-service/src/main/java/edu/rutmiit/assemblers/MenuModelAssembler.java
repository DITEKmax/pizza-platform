package edu.rutmiit.assemblers;

import edu.rutmiit.controllers.MenuController;
import edu.rutmiit.dto.response.MenuItemResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MenuModelAssembler implements RepresentationModelAssembler<MenuItemResponse, EntityModel<MenuItemResponse>> {

    @Override
    public EntityModel<MenuItemResponse> toModel(MenuItemResponse menuItem) {
        return EntityModel.of(menuItem,
                linkTo(methodOn(MenuController.class).getMenuItemById(menuItem.getId())).withSelfRel(),
                linkTo(methodOn(MenuController.class).getAllMenuItems(null, null, 0, 20)).withRel("collection"));
    }
}
