import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './SharedSupply.css';
import RoommateNavBar from '../components/RoommateNavBar';

const SharedSupply = () => {
  const [items, setItems] = useState([]);
  const [showAdd, setShowAdd] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [newItem, setNewItem] = useState({ name: '', quantity: '' });
  const [editItem, setEditItem] = useState({ id: null, name: '', quantity: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const checkAndCreateSupplyList = async () => {
    try {
      const payload = { token: localStorage.getItem('token') };
      const res = await fetch("https://roomie.ddns.net:8080/checkSupplyList", {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      if (!res.ok) throw new Error('Could not initialize supply list');
    } catch (e) {
      console.error('Supply list initialization error:', e);
      throw e;
    }
  };

  const getItems = async () => {
    setLoading(true);
    setError('');
    try {
      await checkAndCreateSupplyList();
      const payload = { token: localStorage.getItem('token') };
      const res = await fetch("https://roomie.ddns.net:8080/getItems", {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (!res.ok) throw new Error('Could not load items');
      const data = await res.json();

      const parsedItems = data.items?.split(',').reduce((acc, row) => {
        const [id, name, amount, lastPurchased] = row.split('|');
        if (!id || !name) return acc;
        acc.push({
          id: +id,
          name: decodeURIComponent(name),
          quantity: +amount,
          lastPurchased
        });
        return acc;
      }, []) || [];

      setItems(parsedItems);
    } catch (e) {
      setError(e.message || 'Failed to load items');
      console.error('Fetch items error:', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getItems();
  }, []);

  const validateItem = (item) => {
    if (!item.name?.trim()) throw new Error('Item name is required');
    if (isNaN(item.quantity)) throw new Error('Quantity must be a number');
    if (item.quantity < 1) throw new Error('Quantity must be at least 1');
  };

  const handleAdd = async () => {
    setLoading(true);
    setError('');
    try {
      const itemToAdd = {
        name: encodeURIComponent(newItem.name.trim()),
        quantity: parseInt(newItem.quantity, 10)
      };

      validateItem(itemToAdd);

      const payload = {
        token: localStorage.getItem('token'),
        item: itemToAdd.name,
        amount: itemToAdd.quantity
      };

      const res = await fetch("https://roomie.ddns.net:8080/addItem", {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Add failed');
      }

      setShowAdd(false);
      setNewItem({ name: '', quantity: '' });
      await getItems();
    } catch (e) {
      setError(e.message);
      console.error('Add item error:', e);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = async () => {
    setLoading(true);
    setError('');
    try {
      const updatedQuantity = parseInt(editItem.quantity, 10);
      if (isNaN(updatedQuantity)) throw new Error('Invalid quantity');
      if (updatedQuantity < 0) throw new Error('Quantity cannot be negative');
      if (!editItem.name.trim()) throw new Error('Item name cannot be empty');

      const payload = {
        token: localStorage.getItem('token'),
        id: editItem.id,
        item: encodeURIComponent(editItem.name.trim()),
        amount: updatedQuantity
      };

      const res = await fetch("https://roomie.ddns.net:8080/editItem", {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Edit failed');
      }

      setShowEdit(false);
      setEditItem({ id: null, name: '', quantity: '' });
      await getItems();
    } catch (e) {
      setError(e.message);
      console.error('Edit item error:', e);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="inventory-container">
      <RoommateNavBar />
      <div className="container py-5">
        <h1 className="text-center mb-4 inventory-title">Shared Supply</h1>
        {error && <div className="alert alert-danger">{decodeURIComponent(error)}</div>}
        {loading && <div className="text-center">Loading…</div>}

        <div className="list-group mb-4">
          {items.map(item => (
            <div key={item.id} className="list-group-item d-flex justify-content-between align-items-center">
              <div className="flex-grow-1">
                <strong>{decodeURIComponent(item.name)}</strong>
                <span className="ms-2 text-muted">x{item.quantity}</span>
                {item.lastPurchased && (
                  <div className="text-muted small mt-1">
                    Last purchased: {new Date(item.lastPurchased).toLocaleDateString()}
                  </div>
                )}
              </div>
              <button
                className="btn btn-sm btn-outline-secondary"
                onClick={() => {
                  setEditItem({
                    id: item.id,
                    name: decodeURIComponent(item.name),
                    quantity: item.quantity
                  });
                  setShowEdit(true);
                }}
              >
                Edit
              </button>
            </div>
          ))}
        </div>

        <button
          className="btn add-btn"
          onClick={() => setShowAdd(true)}
          disabled={loading}
        >
          + Add Item
        </button>

        {/* Add Item Modal */}
        {showAdd && (
          <div className="modal-backdrop-custom">
            <div className="modal-content-custom">
              <h5>Add New Item</h5>
              <input
                className="form-control mb-2"
                placeholder="Name"
                value={newItem.name}
                onChange={e => setNewItem({ ...newItem, name: e.target.value })}
                disabled={loading}
              />
              <input
                className="form-control mb-3"
                placeholder="Quantity"
                type="number"
                min="1"
                value={newItem.quantity}
                onChange={e => setNewItem({ ...newItem, quantity: e.target.value })}
                disabled={loading}
              />
              <div className="d-flex justify-content-end">
                <button
                  className="btn btn-secondary me-2"
                  onClick={() => setShowAdd(false)}
                  disabled={loading}
                >
                  Cancel
                </button>
                <button
                  className="btn btn-primary"
                  onClick={handleAdd}
                  disabled={loading || !newItem.name.trim() || !newItem.quantity}
                >
                  {loading ? 'Saving…' : 'Add'}
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Edit Item Modal */}
        {showEdit && (
          <div className="modal-backdrop-custom">
            <div className="modal-content-custom">
              <h5>Edit Item</h5>
              <input
                className="form-control mb-2"
                value={editItem.name}
                onChange={e => setEditItem({...editItem, name: e.target.value})}
                disabled={loading}
              />
              <input
                className="form-control mb-3"
                type="number"
                min="0"
                value={editItem.quantity}
                onChange={e => setEditItem({...editItem, quantity: e.target.value})}
                disabled={loading}
              />
              <div className="d-flex justify-content-end">
                <button
                  className="btn btn-secondary me-2"
                  onClick={() => setShowEdit(false)}
                  disabled={loading}
                >
                  Cancel
                </button>
                <button
                  className="btn btn-primary"
                  onClick={handleEdit}
                  disabled={loading || isNaN(editItem.quantity) || editItem.quantity < 0 || !editItem.name.trim()}
                >
                  {loading ? 'Saving…' : 'Save'}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SharedSupply;