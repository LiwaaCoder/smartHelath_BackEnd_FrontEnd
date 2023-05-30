package superapp.logic.service.SuperAppObjService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import superapp.Boundary.superAppObjectBoundary;

        @Service
        public interface ObjectsService {
                 superAppObjectBoundary createObject(superAppObjectBoundary obj) throws RuntimeException;


                @Deprecated
                 superAppObjectBoundary updateObject(String obj, String internal_obj_id, superAppObjectBoundary update) throws RuntimeException;

                @Deprecated
                 Optional<superAppObjectBoundary> getSpecificObject(String obj, String internal_obj_id) throws RuntimeException;

                @Deprecated
                 List<superAppObjectBoundary> getAllObjects() throws RuntimeException;

                @Deprecated
                 void deleteAllObjects() throws RuntimeException;

                @Deprecated
                void bindParentAndChild(String parentId, String childId) throws RuntimeException;
        }