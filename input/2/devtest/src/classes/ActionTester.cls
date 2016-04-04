global class ActionTester {
    webservice static Id makeAccount(String aname) {
        Account a = new Account(Name=aname);
        insert a;
        return a.Id;
    }
    
    public Account testAccount { 
        get {
            if (testAccount == null) {
                testAccount = new Account(Name='FOR TEST');
            }
            return testAccount;
        }
        set;
    }
    public Integer clickCount {
        get {
            if (clickCount == null) {
                clickCount = 0;
            }
            return clickCount;
        }
        set;
    }
    public PageReference inc() {
        clickCount++;
        return null;
    }
    public Map<String, String> translations { 
    get {
        if (translations == null) {
            translations = new Map<String, String>();
        }
        return translations;
    } 
    set; }
    public List<String> orderedTranslations { get; private set; }
    
    public String newWord { get; set; }
    public String newTrans { get; set; }
    
    public String test1 = 'ala ma kota';
    public ActionTester() {
        translations.put('drzwi', 'doors');
        translations.put('okno', 'window');
    }
    
    public PageReference deleteUntranslated() {
        Map<String, String> result = new Map<String, String>();
        Integer nullCount = 0;
        
        for (String s : translations.keySet()) {
            if (translations.get(s).length() > 0) {
                result.put(s, translations.get(s));
            } else {
                nullCount++;
            }
        }
        
        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, 'Deleted '+nullCount+' words.'));
        
        translations = result;
        
        return null;
    }
    
    public PageReference orderTranslationsByValue() {
        orderedTranslations = translations.values();
        orderedTranslations.sort();
        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, 'Ordered !!'));
        return null;
    }
    
    public PageReference putTranslation() {
        translations.put(newWord, newTrans);
        return null;
    }
}