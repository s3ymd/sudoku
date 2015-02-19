package sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class Candidates {
	private Set<Integer> set = new HashSet<>();

	public Candidates(int max) {
		for (int i = 1; i <= max; i++) {
			set.add(i);
		}
	}

	public void remove(int num) {
		set.remove(num);
	}

	public void removeAll() {
		set.clear();
	}

	public int count() {
		return set.size();
	}

	public Set<Integer> getNumbers() {
		return set;
	}

	public boolean contains(int n) {
		return set.contains(n);
	}

	public boolean hasOneCandidate() {
		return set.size() == 1;
	}

	public int getOneCandidate() {
		return set.iterator().next();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 9; i++) {
			if (set.contains(i)) {
				sb.append(i);
			} else {
				sb.append('-');
			}
		}
		return sb.toString();
	}
}

class FieldSet {
	private Candidates candidates = new Candidates(9);
	private Set<Field> fieldSet = new HashSet<>();

	public Set<Field> getAllField() {
		return fieldSet;
	}

	public void set(int num) {
		candidates.remove(num);
		for (Field f : fieldSet) {
			f.removeCandidate(num);
		}
	}

	public void addField(Field field) {
		fieldSet.add(field);
	}

	@Override
	public String toString() {
		return candidates.toString();
	}

	public Candidates getCandidates() {
		return candidates;
	}
}

class Field {
	private Candidates candidates = new Candidates(9);
	private FieldSet row;
	private FieldSet column;
	private FieldSet block;

	private int r;
	private int c;

	public Field(int r, int c) {
		this.r = r;
		this.c = c;
	}

	public int getR() {
		return r;
	}

	public int getC() {
		return c;
	}

	private int num;

	public void setRow(FieldSet row) {
		this.row = row;
	}

	public void removeCandidate(int num) {
		candidates.remove(num);
	}

	public void setColumn(FieldSet column) {
		this.column = column;
	}

	public void setBlock(FieldSet block) {
		this.block = block;
	}

	public void set(int num) {
		this.num = num;
		candidates.removeAll();
		row.set(num);
		column.set(num);
		block.set(num);
	}

	public boolean hasNumber() {
		return num > 0;
	}

	public int getNumber() {
		return num;
	}

	public Candidates getCandidates() {
		return candidates;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(num);
		sb.append('/');
		sb.append(candidates);
		return sb.toString();
	}
}

class Matrix {
	private static final int ROWS = 9;
	private static final int COLS = 9;
	private static final int BWIDTH = 3;

	private Field[][] matrix = new Field[ROWS][COLS];
	private List<FieldSet> rowFieldSetList = new ArrayList<>();
	private List<FieldSet> columnFieldSetList = new ArrayList<>();
	private List<FieldSet> blockFieldSetList = new ArrayList<>();
	private Set<Field> allFields = new HashSet<>();

	private List<FieldSet> allFieldSet = new ArrayList<>();

	public Matrix() {

		for (int i = 0; i < ROWS; i++) {
			rowFieldSetList.add(new FieldSet());
			columnFieldSetList.add(new FieldSet());
			blockFieldSetList.add(new FieldSet());
		}

		allFieldSet.addAll(rowFieldSetList);
		allFieldSet.addAll(columnFieldSetList);
		allFieldSet.addAll(blockFieldSetList);

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {

				Field f = new Field(r, c);
				matrix[r][c] = f;
				allFields.add(f);

				FieldSet row = getRow(r);
				row.addField(f);
				f.setRow(row);

				FieldSet col = getColumn(c);
				col.addField(f);
				f.setColumn(col);

				FieldSet block = getBlock(r, c);
				block.addField(f);
				f.setBlock(block);
			}
		}

	}

	public FieldSet getRow(int r) {
		return rowFieldSetList.get(r);
	}

	public FieldSet getColumn(int c) {
		return columnFieldSetList.get(c);
	}

	public FieldSet getBlock(int r, int c) {
		int br = r / BWIDTH;
		int bc = c / BWIDTH;
		return blockFieldSetList.get(br * BWIDTH + bc);
	}

	public void setNumber(int r, int c, int num) {
		matrix[r][c].set(num);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				sb.append(matrix[r][c]).append(' ');
			}
			sb.append(" ");
			sb.append("r:");
			sb.append(getRow(r));
			sb.append("\n");

			if (r % BWIDTH == BWIDTH - 1) {
				for (int c = 0; c < ROWS; c += BWIDTH) {
					sb.append("b:");
					sb.append(getBlock(r, c));
					sb.append("                         ");
				}
				sb.append("\n");
			}
		}
		sb.append("\n");
		for (int c = 0; c < COLS; c++) {
			sb.append("c:");
			sb.append(getColumn(c));
			sb.append(" ");
		}

		return sb.toString();
	}

	int r = 0;

	public void set(String s) {
		for (int c = 0; c < COLS; c++) {
			char ch = s.charAt(c);
			if (ch == ' ') {
				continue;
			}
			int n = ch - '0';
			setNumber(r, c, n);
		}
		r++;
	}

	public Set<Field> getAllFields() {
		return allFields;
	}

	private boolean solve_1() {
		boolean updated = false;

		for (Field f : allFields) {
			if (!f.hasNumber() && f.getCandidates().hasOneCandidate()) {
				int num = f.getCandidates().getOneCandidate();
				f.set(num);
				updated = true;
			}
		}

		return updated;
	}

	private boolean solve_2() {
		boolean updated = false;
		Field field = null;
		int num = 0;

		LOOP: for (FieldSet fs : allFieldSet) {
			for (int n : fs.getCandidates().getNumbers()) {
				List<Field> l = new LinkedList<>();
				for (Field f : fs.getAllField()) {
					if (f.getCandidates().contains(n)) {
						l.add(f);
					}
				}
				if (l.size() == 1) {
					field = l.get(0);
					num = n;
					break LOOP;

				}

			}
		}
		if (field != null) {
			field.set(num);
			updated = true;
		}

		return updated;

	}

	private boolean hasError() {
		for (Field f : allFields) {
			if (!f.hasNumber()) {
				if (f.getCandidates().count() == 0) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean solve_3() {

		boolean updated = false;

		Field field = null;
		int num = 0;

		LOOP: for (Field f : allFields) {
			if (!f.hasNumber()) {
				for (int n : f.getCandidates().getNumbers()) {
					Matrix m = copy();
					m.setNumber(f.getR(), f.getC(), n);
					m.solve();
					if (m.hasError()) {
						field = f;
						num = n;
						updated = true;
						break LOOP;
					}

				}
			}
		}
		if (updated) {
			field.removeCandidate(num);
		}
		return updated;

	}

	public void solve() {
		while (solve_1() || solve_2() || solve_3()) {
			;
		}
	}

	public Matrix copy() {
		Matrix m = new Matrix();
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				if (matrix[r][c].hasNumber()) {
					m.setNumber(r, c, matrix[r][c].getNumber());
				}
			}
		}

		return m;
	}

	public void printResult() {
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				System.out.print(matrix[r][c].getNumber());
			}
			System.out.println();
		}
	}
}

public class Main {

	public static void main(String[] args) {
		Matrix m = new Matrix();
		// m.set("  2 9  6 ");
		// m.set(" 4   1  8");
		// m.set(" 7 42   3");
		// m.set("5     3  ");
		// m.set("  1 6 5  ");
		// m.set("  3     6");
		// m.set("1   57 4 ");
		// m.set("6  9   2 ");
		// m.set(" 2  8 1  ");

		// m.set("    7 6 5");
		// m.set("     4 8 ");
		// m.set("2    6 7 ");
		// m.set(" 73   2  ");
		// m.set("5       4");
		// m.set("  4   96 ");
		// m.set(" 8 4    3");
		// m.set(" 5 2     ");
		// m.set("7 6 8    ");

		m.set("  1   8  ");
		m.set(" 5  1  4 ");
		m.set("   2    7");
		m.set("  7  5 8 ");
		m.set("4   6   9");
		m.set(" 2 4  5  ");
		m.set("3    7   ");
		m.set(" 7  2  9 ");
		m.set("  4   1  ");

		m.solve();
		m.printResult();
	}
}

/*
 * 438172695 617954382 295836471 173649258 569728134 824513967 982465713
 * 351297846 746381529
 */