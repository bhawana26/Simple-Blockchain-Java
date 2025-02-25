import java.security.MessageDigest;
import java.util.ArrayList;

class Block {
    public int index;
    public long timestamp;
    public String transactions;
    public String previousHash;
    public String hash;
    public int nonce;
    public int difficulty;

    public Block(int index, String transactions, String previousHash, int difficulty) {
        this.index = index;
        this.timestamp = System.currentTimeMillis();
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.nonce = 0;
        this.difficulty = difficulty;
        this.hash = mineBlock();
    }

    public String computeHash() {
        String data = index + timestamp + transactions + previousHash + nonce;
        return applySHA256(data);
    }

    public String mineBlock() {
        String target = new String(new char[difficulty]).replace('\0', '0'); 
        while (true) {
            hash = computeHash();
            if (hash.substring(0, difficulty).equals(target)) {
                return hash;
            }
            nonce++;
        }
    }

    public static String applySHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class Blockchain {
    private ArrayList<Block> chain;
    private int difficulty;

    public Blockchain(int difficulty) {
        this.chain = new ArrayList<>();
        this.difficulty = difficulty;
        createGenesisBlock();
    }

    private void createGenesisBlock() {
        Block genesisBlock = new Block(0, "Genesis Block", "0", difficulty);
        chain.add(genesisBlock);
    }

    public void addBlock(String transactions) {
        Block lastBlock = chain.get(chain.size() - 1);
        Block newBlock = new Block(chain.size(), transactions, lastBlock.hash, difficulty);
        chain.add(newBlock);
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.computeHash())) {
                return false;
            }

            if (!currentBlock.previousHash.equals(previousBlock.hash)) {
                return false;
            }
        }
        return true;
    }

    public void tamperChain(int index, String newTransactions) {
        if (index > 0 && index < chain.size()) {
            chain.get(index).transactions = newTransactions;
            chain.get(index).hash = chain.get(index).computeHash();
        }
    }

    public void printBlockchain() {
        for (Block block : chain) {
            System.out.println("\nIndex: " + block.index);
            System.out.println("Timestamp: " + block.timestamp);
            System.out.println("Transactions: " + block.transactions);
            System.out.println("Previous Hash: " + block.previousHash);
            System.out.println("Current Hash: " + block.hash);
            System.out.println("Nonce: " + block.nonce);
        }
    }
}

public class BlockchainSimulation {
    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain(3);

        blockchain.addBlock("Alice pays Bob 10 BTC");
        blockchain.addBlock("Bob pays Charlie 5 BTC");

        System.out.println("\nBlockchain before tampering:");
        blockchain.printBlockchain();

        System.out.println("\nIs blockchain valid? " + blockchain.isChainValid());

        blockchain.tamperChain(1, "Alice pays Bob 100 BTC");

        System.out.println("\nBlockchain after tampering:");
        blockchain.printBlockchain();

        System.out.println("\nIs blockchain valid? " + blockchain.isChainValid());
    }
}
