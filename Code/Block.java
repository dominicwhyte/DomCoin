/*************************************************************************
  * Name: Dominic Whyte
  *
  * Description: Block object with necessary specifications. Main() takes the 
  * hash of the previous block and a prepared transaction from PrepareTransaction 
  * followed by the public key of the miner and creates a Block with these
  * arguments. Also takes Standard Input from ledger.txt file. Make sure to 
  * update your ledger and new Blockchain accordingly! Note: if you are sending
  * money to a new account, make sure you first create the account in the ledger
  * by putting their public key under "Public_Key:" etc. 
  * 
  *****************************************************************************/

public class Block {
    //Symbol table with public key as key and String array storing details of 
    //unused transactions as the value
    private ST<String, Queue<String[]>> ledger; 
    private String previousblock; //the hash of the previous block in blockchain
    private String to;
    private String from;
    private String amount;
    private String hash;
    private String signature;
    private String minerrewardkey; //public key of the miner to receive reward
    private static final int MINING_DIFFICULTY = 4;
    private static final Integer MINING_REWARD = 10;
    
    //constructor to create Block object
    public Block(String previousblock, String to, String from, String amount, 
                 String hash, String signature, String minerrewardkey) {
        this.previousblock = previousblock; //store as instance variable
        //store instance variables
        this.to = to;
        this.from = from;
        this.amount = amount;
        this.hash = hash;
        this.signature = signature;
        this.minerrewardkey = minerrewardkey;
        
        //Read from StdIn file Ledger.txt the latest accurate ledger. From this 
        //ledger, initialize and fill the ledger symbol table 
        ledger = new ST<String, Queue<String[]>>(); //intialize ST
        StdIn.readString(); //read "Public Key:"
        while(!StdIn.isEmpty()) {
            String key = StdIn.readString(); //read the public key String
            StdIn.readString(); //read "Unused Transactions:"
            //make a new queue of String[] for this key
            Queue<String[]> transactions = new Queue<String[]>();
            Boolean moveon = false; //true when it is time to move to next key
            while(!moveon) {
                if(StdIn.isEmpty()) {
                    moveon = true;
                }
                else {
                    String next = StdIn.readString();
                    //end loop if all transactions for this key have been added
                    if(next.matches("Public_Key:")) {
                        moveon = true;
                    }
                    else {
                        //make a new String[] with transaction ID as first item
                        //and transaction coin value as second item
                        String[] unusedtrans = new String[2];
                        unusedtrans[0] = next; //the transaction ID
                        unusedtrans[1] = StdIn.readString(); //coin value
                        //enqueue the newest transaction array
                        transactions.enqueue(unusedtrans);
                    }
                }
            }
            //put into the ledger the public key and all its unused 
            //transactions
            ledger.put(key, transactions);
        }
    }
    //method to print out the ledger
    public void printLedger() {
        for (String key : ledger) {
            System.out.println("Key is: " + key);
            for(String[] transactions : ledger.get(key)) {
                System.out.println("Hash is: " + transactions[0] + " with value of " + transactions[1]);
            }
        }
        
        
    }
    //method to print out the ledger
    public void updateLedger() {
        for (String key : ledger) {
            StdOut.println("Public_Key:");
            StdOut.println(key);
            StdOut.println("Unused_Transactions:");
            for(String[] transactions : ledger.get(key)) {
                StdOut.println(transactions[0] + " " + transactions[1]);
            }
        }
        
        
    }
    //verify the signature and verify the hash
    public Boolean authenticate() {
        //applying 'from' on the signature should yield the hash
        
        //also verify the hash for the transaction
        //String with all text from transaction to be hashed
        String transactiontext = ("To:" + to + "From:" + from +
                                  "Amount:" + amount);
        //hash the transactiontext with Sha256
        String hashedtransactiontext = Sha256.hash(transactiontext);
        if (!hashedtransactiontext.matches(hash)) {
            return false;
        }
        return true;
        
        
    }
    
    //method to print out the new block
    public void printBlock() {
        //Print out instance variables
        System.out.println("Previous_Block_Hash:");
        System.out.println(this.previousblock);
        System.out.println();
        System.out.println("To:");
        System.out.println(this.to);
        System.out.println();
        System.out.println("From:");
        System.out.println(this.from);
        System.out.println();
        System.out.println("Amount:");
        System.out.println(this.amount);
        System.out.println();
        System.out.println("Hash:");
        System.out.println(this.hash);
        System.out.println();
        System.out.println("Signature:");
        System.out.println(this.signature);
        System.out.println();
        
        //figure out which transactions will be used as inputs and what the 
        //outputs will be (ie. how much is returned to the user)
        Double cost = Double.parseDouble(amount); //amount transaction is for
        //Get the queue from the ledger symbol table with the available funds
        //If 'from' public key has no funds, reject transaction
        if (!ledger.contains(from)) {
            throw new RuntimeException("Insufficient funds to complete transaction");
        }
        Queue<String[]> availablefunds = ledger.get(from);
        //Make a Queue with transactions used
        Queue<String[]> usedfunds = new Queue<String[]>();
        Boolean paidfor = false; //is the transaction paid for
        double funds = 0.0; //funds taken out of queue
        //repeat until cost has been covered
        while(!paidfor) {
            //throw an error if there are no more funds
            if (availablefunds.isEmpty()) {
                throw new RuntimeException("Insufficient funds to complete transaction");
            }
            //get the String array with the next unused transaction
            String[] unusedtransaction = availablefunds.dequeue();
            funds = funds + Double.parseDouble(unusedtransaction[1]);
            usedfunds.enqueue(unusedtransaction); //mark as used
            //if the funds are enough to cover the cost, end loop
            if (funds >= cost) {
                paidfor = true;
            }
            //else continue getting new transactions to pay cost
        }
        //update ledger
        ledger.put(from, availablefunds);
        
        //print out which inputs were used
        System.out.println("Inputs:");
        while(!usedfunds.isEmpty()) {
            String[] transactiontobeused = usedfunds.dequeue();
            System.out.println(transactiontobeused[0] + " " + transactiontobeused[1]);
        }
        System.out.println();
        //If the ledger does not contain an entry for the receiver, add it
        if (!ledger.contains(from)) {
            Queue<String[]> newentry = new Queue<String[]>();
            ledger.put(from, newentry);
        }
        //Update the ledger with the new transactions and print outputs
        Double change = funds - cost; //amount to be returned to 'from'
        String[] newtransactionfrom = {hash, change.toString()};
        String[] newtransactionto = {hash, cost.toString()};
        if (change != 0.0) {
        ledger.get(from).enqueue(newtransactionfrom); //add new transaction
        }
        ledger.get(to).enqueue(newtransactionto); //add new transaction
        //print outputs
        System.out.println("Outputs:");
        //By convention, print out the transaction to the 'from' sender first
        System.out.println(newtransactionfrom[0] + " " + newtransactionfrom[1]);
        System.out.println(newtransactionto[0] + " " + newtransactionto[1]);
        System.out.println();
        //Print out public key of the miner (for compensation)
        System.out.println("Miner_Reward_Public_Key:");
        System.out.println(minerrewardkey);
        System.out.println();
        //Give the Miner 10 coins
        //If the Miner does not have any coins, make him an account on the ledger
        String[] reward = {hash, MINING_REWARD.toString()};
        if (!ledger.contains(minerrewardkey)) {
            Queue<String[]> newfunds = new Queue<String[]>();
            newfunds.enqueue(reward);
            ledger.put(minerrewardkey, newfunds);
        }
        else {ledger.get(minerrewardkey).enqueue(reward);}
        //Mining problem solution
        //inputs ommitted in blocktext for simplicity
        String blocktext = ("Previous_Block_Hash:" + this.previousblock + "To:" +
                            to + "From:" + from + "Amount:" + amount + "Hash:" +
                            hash + "Signature:" + signature + "Outputs:" + 
                            newtransactionfrom[0] + " " + newtransactionfrom[1] + 
                            newtransactionto[0] + " " + newtransactionto[1] + 
                            "Miner_Reward_Public_Key:" + minerrewardkey);
        System.out.println("Miner_Solution:"); //solution to mining problem
        String solution = Miner.findkey(MINING_DIFFICULTY, blocktext);
        System.out.println(solution);
        System.out.println();
        System.out.println("Block_Hash:");
        System.out.println(Sha256.hash(blocktext + solution)); 
        System.out.println();
        
    }
    //main method for testing
    public static void main(String[] args) {
        String previous = args[0]; //previous block hash
        String to = args[2]; //"to" public key
        String from = args[4]; //"from" public key
        String amount = args[6]; //amount transaction is for
        String hash = args[8]; //hash
        String signature = args[10]; //signature
        String minerrewardkey = args[11]; //public key of miner for compensation
        
        Block block = new Block(previous, to, from, amount, hash, signature, minerrewardkey);
        //check signature and hash
        if (block.authenticate())
            System.out.println("Authentication Success");
        else
            System.out.println("Authentication Failure");
        if (block.authenticate()) {
            System.out.println("New Block:");
            System.out.println();
            block.printBlock();
            //update ledger only if transaction was verified 
            
            System.out.println("Updated Ledger:");
            System.out.println();
            block.updateLedger();
        }
    }
}

