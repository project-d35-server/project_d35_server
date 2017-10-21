package fr.univtln.project.d35.server.resource;

import fr.univtln.project.d35.server.crud.CrudServiceBean;
import fr.univtln.project.d35.server.exception.ResourceException;
import fr.univtln.project.d35.server.response.RestResponse;

import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Resource {
    public static CrudServiceBean crudService;
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_JSON_UTF8 = "application/json;charset=urf-8";
    public static final String APPLICATION_URLENCODED = "application/x-www-form-urlencoded";
    public Class<?> entity;

    public Resource() {
        crudService = new CrudServiceBean(CrudServiceBean.PU_DOCKER_POSTGRES);
    }

    public Resource(CrudServiceBean crudService) {
        crudService = crudService;
    }

    public void setCrudService(CrudServiceBean crudServiceBean) {
        crudService = crudServiceBean;
    }

    public <T> Response get(Class<T> type, long id) throws ResourceException {
        RestResponse<T> response = new RestResponse();
        crudService.newTransaction();
        Response toReturn;
        if (crudService.exist(type, id)) {
            response.setData(crudService.find(type, id));
            toReturn = response.throw200Ok();
        } else {
            response.setErrorMessage("Not Found");
            toReturn = response.throw404NotFound();
        }

        crudService.closeTransaction();
        //this.LOG.info(toReturn.toString());
        return toReturn;
    }

    public <T> Response fetch(Class<T> type) {
        return (new RestResponse(200, crudService.findAll(type), (String)null)).throw200Ok();
    }

    public <T> Response create(T t) {
        /*ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(t, new Class[0]);
        Iterator var5 = violations.iterator();

        while(var5.hasNext()) {
            ConstraintViolation<T> violation = (ConstraintViolation)var5.next();
            this.LOG.warning(violation.getMessage());
        }

        System.out.println("violations : " + violations.equals((Object)null));
        System.out.println("violations size : " + violations.size());*/
        RestResponse<T> response = new RestResponse();
        Response toReturn;
        if (t != null) {
            try {
                crudService.newTransaction();
                response.setData(crudService.create(t));
                crudService.commit();
                toReturn = response.throw201Created();
            } catch (Exception var8) {
                var8.printStackTrace();
                response.setErrorMessage("Conflict");
                toReturn = response.throw409Conflict();
            }
        } else {
            response.setErrorMessage("Forbidden");
            toReturn = response.throw403Forbidden();
        }

        crudService.closeTransaction();
        //this.LOG.info(toReturn.toString());
        return toReturn;
    }

    public <T> Response update(T t, long id) throws ResourceException {
        /*ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(t, new Class[0]);
        Iterator var7 = violations.iterator();

        while(var7.hasNext()) {
            ConstraintViolation<T> violation = (ConstraintViolation)var7.next();
            this.LOG.warning(violation.getMessage());
        }*/

        RestResponse<T> response = new RestResponse();
        Response toReturn;
        if (t != null) {
            crudService.newTransaction();
            if (crudService.exist(t.getClass(), id)) {
                response.setData(crudService.update(t));
                crudService.commit();
                toReturn = response.throw200Ok();
            } else {
                response.setErrorMessage("Not Found");
                toReturn = response.throw404NotFound();
            }
        } else {
            response.setErrorMessage("Forbidden");
            toReturn = response.throw403Forbidden();
        }

        crudService.closeTransaction();
        //this.LOG.info(toReturn.toString());
        return toReturn;
    }

    public <T> Response delete(Class<T> type, long id) throws ResourceException {
        RestResponse<T> response = new RestResponse();
        Response toReturn;
        if (crudService.exist(type, id)) {
            crudService.newTransaction();
            crudService.delete(type, id);
            crudService.commit();
            toReturn = response.throw204NoContent();
        } else {
            response.setErrorMessage("Not Found");
            toReturn = response.throw404NotFound();
        }

        crudService.closeTransaction();
        //this.LOG.info(toReturn.toString());
        return toReturn;
    }
}
