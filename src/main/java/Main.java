import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        List<PurchaseList> purchaseLists = session.createQuery("from PurchaseList", PurchaseList.class).getResultList();
        for (PurchaseList purchaseList : purchaseLists) {
            int studentId = getStudentId(session, purchaseList.getStudentName());
            int courseId = getCourseId(session, purchaseList.getCourseName());

            LinkedPurchaseListId id = new LinkedPurchaseListId(studentId, courseId);
            LinkedPurchaseList linkedPurchaseList = new LinkedPurchaseList(id, studentId, courseId);
            session.persist(linkedPurchaseList);
        }

        transaction.commit();
        session.close();
        sessionFactory.close();
    }

    private static int getStudentId(Session session, String studentName) {
        String query = "SELECT s.id FROM Student s WHERE s.name = :name";
        return session.createQuery(query, Integer.class)
                .setParameter("name", studentName)
                .getSingleResult();
    }

    private static int getCourseId(Session session, String courseName) {
        String query = "SELECT c.id FROM Course c WHERE c.name = :name";
        return session.createQuery(query, Integer.class)
                .setParameter("name", courseName)
                .getSingleResult();
    }


}
