package generator;

import java.util.Arrays;
import java.util.Random;

public class SeqGenSingleSequenceMultipleRepeats extends SequenceGenerator
{
	
	@Override
	public String generateSequence(int m, int r, int l)
	{
		String repeatedSequence = generateSequence(SequenceGenerator.NUCLEOTIDES, l);
		Random random = new Random();
		StringBuilder sb = new StringBuilder(m);
		int[] repeatedSequenceIndices = new int[r];
		int nonRepeatedLength = m - r * l;
		if (nonRepeatedLength > 0)
		{
			for (int i = 0; i < r; i++)
			{
				repeatedSequenceIndices[i] = random.nextInt(nonRepeatedLength);
			}
			Arrays.sort(repeatedSequenceIndices);
			sb.append(generateSequence(SequenceGenerator.NUCLEOTIDES, nonRepeatedLength));
		}
		for (int i = 0; i < r; i++)
		{
			sb.insert(i * l + repeatedSequenceIndices[i], repeatedSequence);
		}
		return sb.toString();
	}
	
	/**
	 * XXX: TEMPORARY
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length < 3)
		{
			System.err.printf("*** Usage: %s m r l", SeqGenSingleSequenceMultipleRepeats.class.getCanonicalName());
			System.exit(1);
		}
		int m = Integer.parseInt(args[0]);
		int r = Integer.parseInt(args[1]);
		int l = Integer.parseInt(args[2]);
		SequenceGenerator generator = new SeqGenSingleSequenceMultipleRepeats();
		generator.setDebugOutput(true);
		String generated = generator.generateSequence(m, r, l);
		System.out.println(generated);
	}
}
