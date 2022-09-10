package cn.org.opendfl.translateDemo.dflsystem.biz;

public class Test {
    public static int[] twoSum(Integer[] nums, int target) {
        int[] result = new int[2];

        for (int i = 0; i < nums.length; i++) {
            for (int j = i; j < nums.length; j++) {
                if (i != j && nums[i] + nums[j] == target) {
                    result[0] = i;
                    result[1] = j;
                    return result;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        Integer[] nums = Arrays.array(3, 2, 4);
//        int[] result = twoSum(nums, 6);
//        System.out.println(result[0] + "," + result[1]);
//        ListNode l1=
        Test test=new Test();
        ListNode node1=test.toNode(1234);
        test.printNode(node1);



    }

    private ListNode toNode(int v){
        ListNode node=null;
        int length=(""+v).length();
        for(int i=0; i<length; i++){
            int t=v%10;
            v=v/10;
//            System.out.println(t);
            node=new ListNode(t, node);
        }
        return node;
    }

    private void printNode(ListNode node){

        while(node.next!=null){
            node=node.next;
            System.out.print(node.val);
        }
        if(node.next==null){
            System.out.print(node.val);
        }
        System.out.println();
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode root = new ListNode(0);
        ListNode cursor = root;
        int carry = 0;
        while(l1 != null || l2 != null || carry != 0) {
            int l1Val = l1 != null ? l1.val : 0;
            int l2Val = l2 != null ? l2.val : 0;
            int sumVal = l1Val + l2Val + carry;
            carry = sumVal / 10;

            ListNode sumNode = new ListNode(sumVal % 10);
            cursor.next = sumNode;
            cursor = sumNode;

            if(l1 != null) l1 = l1.next;
            if(l2 != null) l2 = l2.next;
        }

        return root.next;
    }

}

/**
 * Definition for singly-linked list.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}

