package edu.rutmiit.controllers;

import edu.rutmiit.assemblers.MenuModelAssembler;
import edu.rutmiit.dto.request.MenuItemRequest;
import edu.rutmiit.dto.request.PatchMenuItemRequest;
import edu.rutmiit.dto.request.UpdateMenuItemRequest;
import edu.rutmiit.dto.response.MenuItemResponse;
import edu.rutmiit.dto.response.PagedResponse;
import edu.rutmiit.endpoints.MenuApi;
import edu.rutmiit.service.MenuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MenuController implements MenuApi {

    private final MenuService menuService;
    private final MenuModelAssembler menuModelAssembler;
    private final PagedResourcesAssembler<MenuItemResponse> pagedAssembler;

    public MenuController(MenuService menuService, MenuModelAssembler menuModelAssembler, PagedResourcesAssembler<MenuItemResponse> pagedAssembler) {
        this.menuService = menuService;
        this.menuModelAssembler = menuModelAssembler;
        this.pagedAssembler = pagedAssembler;
    }


    @Override
    public EntityModel<MenuItemResponse> getMenuItemById(Long id) {
        return menuModelAssembler.toModel(menuService.findMenuItemById(id));
    }

    @Override
    public PagedModel<EntityModel<MenuItemResponse>> getAllMenuItems(Boolean availableOnly, String nameSearch, int page, int size) {
        PagedResponse<MenuItemResponse> paged = menuService.findAllMenuItems(availableOnly, nameSearch, page, size);
        Page<MenuItemResponse> springPage = new PageImpl<>(
                paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()),
                paged.totalElements()
        );
        return pagedAssembler.toModel(springPage, menuModelAssembler);
    }

    @Override
    public ResponseEntity<EntityModel<MenuItemResponse>> createMenuItem(MenuItemRequest request) {
        MenuItemResponse created = menuService.createMenuItem(request);
        EntityModel<MenuItemResponse> model = menuModelAssembler.toModel(created);
        return ResponseEntity.created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public EntityModel<MenuItemResponse> updateMenuItem(Long id, UpdateMenuItemRequest request) {
        return menuModelAssembler.toModel(menuService.updateMenuItem(id, request));
    }

    @Override
    public EntityModel<MenuItemResponse> patchMenuItem(Long id, PatchMenuItemRequest request) {
        return  menuModelAssembler.toModel(menuService.patchMenuItem(id, request));
    }

    @Override
    public void deleteMenuItem(Long id) {
        menuService.deleteMenuItem(id);
    }
}
