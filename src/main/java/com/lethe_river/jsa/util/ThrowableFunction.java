package com.lethe_river.jsa.util;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 検査例外を発生させる可能性のある1引数関数
 * @author YuyaAizawa
 *
 * @param <T> 引数の型
 * @param <R> 戻り値の型
 * @param <E> 例外の型
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Exception> {

	/**
	 * 検査例外を発生させる関数を指定し，実行時例外にラップした関数を生成する．
	 * @param f 検査例外を発生させる関数
	 * @param <T> 引数の型
	 * @param <R> 戻り値の型
	 * @return 検査例外を発生させない関数
	 */
	public static <T,R> Function<T, R> unchecked(ThrowableFunction<T, R, ?> f) {
		return f.unchecked();
	}

	/**
	 * 検査例外を発生させる関数と，発生した例外を戻り値に変換する関数を指定し，例外を発生させない関数を生成する．
	 * @param f 検査例外を発生させる関数
	 * @param c 発生した例外とその時の引数から戻り値を生成する関数
	 * @param <T> 引数の型
	 * @param <R> 戻り値の型
	 * @param <E> 例外の型
	 * @return 例外を発生させない関数
	 */
	public static <T, R, E extends Exception> Function<T, R> complement(ThrowableFunction<T, R, E> f, BiFunction<E, T ,R> c) {
		return f.complement(c);
	}

	/**
	 * 検査例外を発生させる関数を指定し，結果または例外を返す関数を生成する．
	 * @param f 検査例外を発生させる関数
	 * @param <T> 引数の型
	 * @param <R> 戻り値の型
	 * @param <E> 例外の型
	 * @return 結果または例外を返す関数
	 */
	public static <T, R, E extends Exception> Function<T, Result<R, E>> includesToValue(ThrowableFunction<T, R, E> f) {
		return f.includesToValue();
	}

	/**
	 * 検査例外を発生させる関数を指定し，結果をOptional(例外発生時はempty)として返す関数を生成する．
	 * @param f 検査例外を発生させる関数
	 * @param <T> 引数の型
	 * @param <R> 戻り値の型
	 * @param <E> 例外の型
	 * @return 結果をOptionalとして返す関数
	 */
	public static <T, R, E extends Exception> Function<T, Optional<R>> maybe(ThrowableFunction<T, R, E> f) {
		return f.maybe();
	}

	/**
	 * 検査例外を発生させる関数を指定し，結果をStreamとして返す関数を生成する．例外発生時Streamは空となる.
	 * @param f 検査例外を発生させる関数
	 * @param <T> 引数の型
	 * @param <R> 戻り値の型
	 * @param <E> 例外の型
	 * @return 結果をStreamとして返す関数
	 */
	public static <T, R, E extends Exception> Function<T, Stream<R>> ignored(ThrowableFunction<T, R, E> f) {
		return f.includesToValue()
				.andThen(u -> u.map(
						(Function<R, Stream<R>>) r -> Stream.of(r),
						(Function<E, Stream<R>>) e -> Stream.empty()));
	}

	/**
	 * 引数を関数に適用し戻り値を返す．検査例外が発生する可能性がある.
	 *
	 * @param t この関数に適用する引数
	 * @return この関数の戻り値
	 * @throws E この関数が発生させる例外
	 */
	public R apply(T t) throws E;

	/**
	 * 例外が発生したときの戻り値を与える関数を指定し，この関数と合成した関数を返す.
	 *
	 * @param complementer 発生した例外とその時の引数から戻り値を生成する関数
	 * @return 検査例外を発生させない関数
	 */
	public default Function<T, R> complement(BiFunction<? super E, ? super T, ? extends R> complementer) {
		return v -> {
			try {
				return apply(v);
			} catch (RuntimeException | Error e) {
				throw e;
			} catch (Exception e) {
				return complementer.apply((E) e, v);
			}
		};
	}

	/**
	 * 発生した例外を実行時例外にラップする関数を返す．
	 * @return 検査例外を発生させない関数
	 */
	public default Function<T, R> unchecked() {
		return v -> {
			try {
				return apply(v);
			} catch (RuntimeException | Error e) {
				throw e;
			} catch (Exception e) {
				if(e instanceof IOException) {
					throw new UncheckedIOException((IOException) e);
				}
				throw new RuntimeException(e);
			}
		};
	}

	/**
	 * 引数を関数に適用した結果か，発生した例外のどちらかを戻り値とする関数を返す.
	 * @return 検査例外を発生させない関数
	 */
	public default Function<T, Result<R, E>> includesToValue() {
		return v -> {
			try {
				return Result.ok(apply(v));
			} catch (RuntimeException | Error e) {
				throw e;
			} catch (Exception e) {
				return Result.error(((E) e));
			}
		};
	}

	/**
	 * 引数を関数に適用した結果をOptionalでラップする関数を返す．例外が発生した場合はempty.
	 * @return 検査例外を発生させない関数
	 */
	public default Function<T, Optional<R>> maybe() {
		return v -> {
			try {
				return Optional.ofNullable(apply(v));
			} catch (RuntimeException | Error e) {
				throw e;
			} catch (Exception e) {
				return Optional.empty();
			}
		};
	}
}
