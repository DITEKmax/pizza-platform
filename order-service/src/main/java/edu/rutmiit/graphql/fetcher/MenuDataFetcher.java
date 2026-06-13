package edu.rutmiit.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.dto.request.MenuItemRequest;
import edu.rutmiit.dto.request.PatchMenuItemRequest;
import edu.rutmiit.dto.request.UpdateMenuItemRequest;
import edu.rutmiit.dto.response.MenuItemResponse;
import edu.rutmiit.dto.response.PagedResponse;
import edu.rutmiit.graphql.types.*;
import edu.rutmiit.service.MenuService;

@DgsComponent
public class MenuDataFetcher {

    private final MenuService menuService;

    public MenuDataFetcher (MenuService menuService){
        this.menuService = menuService;
    }

    @DgsQuery
    public MenuItemResponse menuItem(@InputArgument String id){
        return menuService.findMenuItemById(Long.parseLong(id));
    }

    @DgsQuery
    public MenuItemConnectionGql menuItems(
            @InputArgument MenuItemFilterGql filter,
            @InputArgument Integer page,
            @InputArgument Integer size) {

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        Boolean available = (filter != null) ? filter.available() : null;
        String name = (filter != null) ? filter.name() : null;

        PagedResponse<MenuItemResponse> paged =
                menuService.findAllMenuItems(available, name, pageNum, pageSize);

        return new MenuItemConnectionGql(
                paged.content(),
                new PageInfoGql(paged.pageNumber(), paged.pageSize(), paged.totalPages(), paged.last()),
                paged.totalElements()
        );
    }

    @DgsMutation
    public MenuItemResponse createMenuItem(@InputArgument CreateMenuItemInputGql input){
        MenuItemRequest request = new MenuItemRequest(
                input.name(),
                input.description(),
                input.price(),
                input.available()
        );

        return menuService.createMenuItem(request);
    }

    @DgsMutation
    public MenuItemResponse updateMenuItem(@InputArgument String id, @InputArgument UpdateMenuItemInputGql input){
        UpdateMenuItemRequest request = new UpdateMenuItemRequest(
                input.name(),
                input.description(),
                input.price(),
                input.available()
        );

        return menuService.updateMenuItem(Long.parseLong(id), request);
    }

    @DgsMutation
    public MenuItemResponse patchMenuItem(@InputArgument String id, @InputArgument PatchMenuItemInputGql input){
        PatchMenuItemRequest request = new PatchMenuItemRequest(
                input.name(),
                input.description(),
                input.price(),
                input.available()
        );

        return menuService.patchMenuItem(Long.parseLong(id), request);
    }

    @DgsMutation
    public boolean deleteMenuItem(@InputArgument String id){
        menuService.deleteMenuItem(Long.parseLong(id));
        return true;
    }
}
