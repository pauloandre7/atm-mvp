package br.edu.utfpr.pauloandre7.repositories;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import br.edu.utfpr.pauloandre7.models.Conta;

public class ContaRepository implements IContaRepository {

	private final List<Conta> contas = new ArrayList<>();
	private final AtomicLong sequenciaId = new AtomicLong(1L);

	@Override
	public synchronized boolean create(Conta conta) {
		if (conta == null || conta.getNumeroConta() == null) {
			return false;
		}

		if (findByNumConta(conta.getNumeroConta()) != null) {
			return false;
		}

		Long id = conta.getId();
		if (id == null) {
			id = sequenciaId.getAndIncrement();
			if (!atribuirId(conta, id)) {
				return false;
			}
		} else {
			atualizarSequencia(id);
		}

		if (findById(id) != null) {
			return false;
		}

		contas.add(conta);
		return true;
	}

	@Override
	public synchronized boolean delete(Conta conta) {
		if (conta == null) {
			return false;
		}

		for (int indice = 0; indice < contas.size(); indice++) {
			Conta atual = contas.get(indice);
			boolean mesmoId = conta.getId() != null && conta.getId().equals(atual.getId());
			boolean mesmoNumero = conta.getNumeroConta() != null && conta.getNumeroConta().equals(atual.getNumeroConta());

			if (mesmoId || mesmoNumero) {
				contas.remove(indice);
				return true;
			}
		}

		return false;
	}

	@Override
	public synchronized Conta findById(Long id) {
		if (id == null) {
			return null;
		}

		for (Conta conta : contas) {
			if (id.equals(conta.getId())) {
				return conta;
			}
		}

		return null;
	}

	@Override
	public synchronized Conta findByNumConta(String numConta) {
		if (numConta == null) {
			return null;
		}

		for (Conta conta : contas) {
			if (numConta.equals(conta.getNumeroConta())) {
				return conta;
			}
		}

		return null;
	}

	@Override
	public synchronized List<Conta> findAll() {
		return new ArrayList<>(contas);
	}

	private void atualizarSequencia(Long id) {
		sequenciaId.updateAndGet(atual -> Math.max(atual, id + 1));
	}

	private boolean atribuirId(Conta conta, Long id) {
		try {
			Field campoId = Conta.class.getDeclaredField("id");
			campoId.setAccessible(true);
			campoId.set(conta, id);
			return true;
		} catch (ReflectiveOperationException exception) {
			return false;
		}
	}
}
