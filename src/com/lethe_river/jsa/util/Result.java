package com.lethe_river.jsa.util;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Result<O, E extends Exception> {

	private interface Member<O, E> extends Serializable {
		<R> R map(
				Function<? super O, ? extends R> f1,
				Function<? super E, ? extends R> f2);

		void consume(
				Consumer<? super O> c1,
				Consumer<? super E> c2);

		Object getValue();
	}

	private final Member<O, E> member;

	/**
	 * 成功の要素を持つ新しいUnion2を返す．
	 *
	 * @param value
	 * @return 新しいUnion2
	 * @exception NullPointerException valueがnullの場合
	 */
	public static <O, E extends Exception> Result<O, E> ok(O value) {
		return new Result<>(new Ok<>(Objects.requireNonNull(value)));
	}

	/**
	 * 失敗の要素を持つ新しいRusultを返す．
	 *
	 * @param value
	 * @return 新しいUnion2
	 * @exception NullPointerException valueがnullの場合
	 */
	public static <O, E extends Exception> Result<O, E> error(E value) {
		return new Result<>(new Error<>(Objects.requireNonNull(value)));
	}

	/**
	 * 結果に関数を適用し，結果を返す． 与えたそれぞれの関数のうち，要素の型に対する関数が適用され，結果が返る．
	 * 与える関数の戻り値の型は一致していなければならない．
	 *
	 * @param f1 T1に適用する関数
	 * @param f2 T2に適用する関数
	 * @return 関数の戻り値
	 */
	public <R> R map(
			Function<? super O, ? extends R> f1,
			Function<? super E, ? extends R> f2) {
		return member.map(f1, f2);
	}

	/**
	 * 要素に対してオペレーションを実行する． 与えたそれぞれのオペレーションのうち，要素の型に対するオペレーションが実行される．
	 *
	 * @param c1 T1に対するオペレーション
	 * @param c2 T2に対するオペレーション
	 */
	public void consume(
			Consumer<? super O> c1,
			Consumer<? super E> c2) {
		member.consume(c1, c2);
	}

	/**
	 * 要素の文字列表現を返す.
	 *
	 * @return 要素の文字列表現
	 */
	@Override
	public String toString() {
		return map(O::toString, E::toString);
	}

	/**
	 * 要素に基づくハッシュを返す．
	 *
	 * @return 要素に基づくハッシュ
	 */
	@Override
	public int hashCode() {
		return member.getValue().hashCode();
	}

	/**
	 * 指定されたオブジェクトがこのRusultと等しいか比較する． 指定されたオブジェクトもUnion2であり，内部の要素が等しいときにtrueをかえす．
	 *
	 * @return 指定されたオブジェクトがこのRusultと等しい場合は true
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Object other = ((Result) obj).member.getValue();
		return member.getValue().equals(other);
	}

	private Result(Member<O, E> member) {
		this.member = member;
	}

	private static class Ok<O, E> implements Member<O, E> {
		private final O value;

		Ok(O value) {
			this.value = value;
		}

		@Override
		public <R> R map(
				Function<? super O, ? extends R> f1,
				Function<? super E, ? extends R> f2) {
			return f1.apply(value);
		}

		@Override
		public void consume(
				Consumer<? super O> c1,
				Consumer<? super E> c2) {
			c1.accept(value);
		}

		@Override
		public Object getValue() {
			return value;
		}
	}

	private static class Error<O, E> implements Member<O, E> {
		private final E value;

		Error(E value) {
			this.value = value;
		}

		@Override
		public <R> R map(
				Function<? super O, ? extends R> f1,
				Function<? super E, ? extends R> f2) {
			return f2.apply(value);
		}

		@Override
		public void consume(
				Consumer<? super O> c1,
				Consumer<? super E> c2) {
			c2.accept(value);
		}

		@Override
		public Object getValue() {
			return value;
		}
	}
}
