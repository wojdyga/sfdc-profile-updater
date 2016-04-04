trigger CaseTrigger on PreCase__c (before update) {
    Precase__c pc = Trigger.new[0];
    Precase__c oldpc = Trigger.old[0];
    if ((pc.Trigger__c != oldpc.Trigger__c) && (pc.Trigger__c == 'CreateCase')) {
        Case c = new Case(Subject=pc.Name, Description=pc.OwnerId);
        insert c;
        pc.Case__c = c.Id;
    }
}