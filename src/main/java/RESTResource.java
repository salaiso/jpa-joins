import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/")
@Stateless
public class RESTResource {
  
  @PersistenceContext(unitName = "ds")
  private EntityManager em;
  
  @GET
  @Path("/buckets")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getBuckets() {
    
    TypedQuery<Bucket> q = em.createQuery("SELECT bu FROM Bucket bu", Bucket.class);
    List<String> ret = q.getResultList().stream().map(b -> b.id).collect(Collectors.toList());
    return Response.ok().entity(ret).build();
  }
  
  @GET 
  @Path("/bucket/{bucketId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getBucket(@PathParam("bucketId") String bucketId) {
      
    return Response.ok().entity(em.find(Bucket.class, bucketId)).build();
  }
  
  @POST
  @Path("bucket/{bucketId}/plop")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addToBucket(@PathParam("bucketId") String bucketId, Plop plop) {
    
    Bucket bucket = em.find(Bucket.class, bucketId);
    if (bucket == null)
      return Response.status(Status.BAD_REQUEST).build();
    
    plop.id = UUID.randomUUID().toString();
    bucket.plops.add(plop);
    em.merge(bucket);
    
    return Response.ok().build();
  }
}
