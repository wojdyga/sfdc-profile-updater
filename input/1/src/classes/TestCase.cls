public class TestCase {

    Id caseId;
    
    public TestCase() {
        caseId = ApexPages.currentPage().getParameters().get('caseId');
    }
    
    public String getCaseTitle() {
        return [SELECT Subject FROM Case WHERE Id=:caseId LIMIT 1].Subject;
    }


    public String getCaseId() {
        return caseId;
    }

}