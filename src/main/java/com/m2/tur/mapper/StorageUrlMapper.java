package com.m2.tur.mapper;

import com.m2.tur.config.SupabaseConfig;
import com.m2.tur.model.entity.Photo;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StorageUrlMapper {

    private final SupabaseConfig supabaseConfig;

    @Named("fullUrl")
    public String fullUrl(Photo photo) {
        return supabaseConfig.getUrlPublic() + supabaseConfig.getBucket() + "/" + photo.getPath();
    }
}
