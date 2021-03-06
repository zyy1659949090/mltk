package mltk.predictor.tree.ensemble.brt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Test;

import mltk.predictor.io.PredictorReader;
import mltk.predictor.tree.RegressionTree;
import mltk.predictor.tree.RegressionTreeLeaf;
import mltk.predictor.tree.RegressionTreeTestHelper;
import mltk.predictor.tree.TreeInteriorNode;
import mltk.predictor.tree.ensemble.BoostedRTrees;
import mltk.util.MathUtils;

public class BRTTest {

	@Test
	public void testIO() {
		RegressionTree tree1 = RegressionTreeTestHelper.getInstance().getTree1();
		RegressionTree tree2 = RegressionTreeTestHelper.getInstance().getTree2();
		BoostedRTrees bt = new BoostedRTrees();
		bt.add(tree1);
		bt.add(tree2);
		
		BRT brt = new BRT(1);
		brt.trees[0] = bt;
		
		try {
			ByteArrayOutputStream boas = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(boas);
			brt.write(out);
			out.close();
			
			ByteArrayInputStream bais = new ByteArrayInputStream(boas.toByteArray());
			BufferedReader in = new BufferedReader(new InputStreamReader(bais));
			
			BRT b = PredictorReader.read(in, BRT.class);
			BoostedRTrees ts = b.getRegressionTreeList(0);
			
			Assert.assertEquals(2, ts.size());
			Assert.assertTrue(ts.get(0) instanceof RegressionTree);
			Assert.assertTrue(ts.get(1) instanceof RegressionTree);
			RegressionTree t1 = (RegressionTree) ts.get(0);
			
			Assert.assertTrue(t1.getRoot() instanceof TreeInteriorNode);
			TreeInteriorNode root = (TreeInteriorNode) t1.getRoot();
			Assert.assertEquals(1, root.getSplitAttributeIndex());
			Assert.assertEquals(0.5, root.getSplitPoint(), MathUtils.EPSILON);
			Assert.assertTrue(root.getLeftChild() instanceof RegressionTreeLeaf);
			Assert.assertTrue(root.getRightChild() instanceof TreeInteriorNode);
		} catch (Exception e) {
			Assert.fail("Should not see exception: " + e.getMessage());
		}
	}

}
