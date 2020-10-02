import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
    
    /* [eclipselink.sql] SELECT ID FROM bucket */
    TypedQuery<Bucket> q = em.createQuery("SELECT bu FROM Bucket bu", Bucket.class);
    List<String> ret = q.getResultList().stream().map(b -> b.id).collect(Collectors.toList());
    return Response.ok().entity(ret).build();
  }
  
  @GET 
  @Path("/bucket/{bucketId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getBucket(@PathParam("bucketId") String bucketId) {
      
    /* [eclipselink.sql] SELECT ID, bucket_id, CONTENT FROM plop WHERE (bucket_id = ?) */
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
    
    /* [eclipselink.sql] SELECT ID, bucket_id, CONTENT FROM plop WHERE (ID = ?) */
    /* [eclipselink.sql] INSERT INTO plop (ID, bucket_id, CONTENT) VALUES (?, ?, ?) */
    /* [eclipselink.sql] UPDATE plop SET bucket_id = ? WHERE (ID = ?) */
    em.merge(bucket);
    
    return Response.ok().build();
  }
  
  @POST
  @Path("bucket")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response makeBucket() {
    
    /* [eclipselink.sql] INSERT INTO bucket (ID) VALUES (?) */
    em.persist(new Bucket());
    return Response.ok().build();
  }
  
  @DELETE
  @Path("bucket/{bucketId}/plop/{plopId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeFromBucket(
      @PathParam("bucketId") String bucketId,
      @PathParam("plopId") String plopId) {
    
    Bucket bucket = em.find(Bucket.class, bucketId);
    if (bucket == null)
      return Response.status(Status.BAD_REQUEST).build();
    
    /* Important: if bucket.plops is assigned a _new_ list object, this will do nothing */
    bucket.plops.removeIf(p -> p.id.equals(plopId));

    /* [eclipselink.sql] DELETE FROM plop WHERE (ID = ?) */
    em.merge(bucket);

    return Response.ok().build();
  }
  
  @DELETE
  @Path("bucket/{bucketId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeBucket(
      @PathParam("bucketId") String bucketId) {
    
    Bucket bucket = em.find(Bucket.class, bucketId);
    if (bucket == null)
      return Response.status(Status.BAD_REQUEST).build();
    
    /* [eclipselink.sql] DELETE FROM bucket WHERE (ID = ?) */
    em.remove(bucket);
    return Response.ok().build();
  }
}
