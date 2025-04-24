import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './SharedSupply.css';
import RoommateNavBar from '../components/RoommateNavBar';

const SharedSupply = () => {
  const [items, setItems] = useState([]);
  const [showAdd, setShowAdd] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [newItem, setNewItem] = useState({ name: '', quantity: '' });
  const [editItem, setEditItem] = useState({ id: null, quantity: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const checkAndCreateSupplyList = async () => {
    const res = await fetch('https://roomie.ddns.net/checkAndCreateSupplyList', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token: localStorage.getItem('token') }),
    });
    if (!res.ok) throw new Error('Could not initialize supply list');
  };

  const getItems = async () => {
    setLoading(true);
    setError('');
    try {
      await checkAndCreateSupplyList();
      const res = await fetch('https://roomie.ddns.net/getItems', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ token: localStorage.getItem('token') }),
      });
      if (!res.ok) throw new Error('Could not load items');
      const data = await res.json();
      const arr = data.items
        ? data.items.split(',').map(row => {
            const [id, name, amount, lastPurchased] = row.split('|');
            return { id: +id, name, quantity: +amount, lastPurchased };
          })
        : [];
      setItems(arr);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { getItems(); }, []);

  const handleAdd = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await fetch('https://roomie.ddns.net/addItem', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          token: localStorage.getItem('token'),
          item: newItem.name.trim(),
          amount: parseInt(newItem.quantity, 10)
        }),
      });
      if (!res.ok) throw new Error('Add failed');
      setShowAdd(false);
      setNewItem({ name: '', quantity: '' });
      await getItems();
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await fetch('https://roomie.ddns.net/editItem', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          token: localStorage.getItem('token'),
          id: editItem.id,
          item: items.find(i => i.id === editItem.id).name,
          amount: parseInt(editItem.quantity, 10)
        }),
      });
      if (!res.ok) throw new Error('Edit failed');
      setShowEdit(false);
      setEditItem({ id: null, quantity: '' });
      await getItems();
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="inventory-container">
      <RoommateNavBar />
      <div className="container py-5">
        <h1 className="text-center mb-4 inventory-title">Shared Supply</h1>
        {error && <div className="alert alert-danger">{error}</div>}
        {loading && <div className="text-center">Loading…</div>}

        <div className="list-group mb-4">
          {items.map(item => (
            <div key={item.id} className="list-group-item d-flex justify-content-between">
              <div><strong>{item.name}</strong> x{item.quantity}</div>
              <div>
                <button className="btn btn-sm btn-outline-secondary me-2"
                        onClick={() => { setEditItem({ id: item.id, quantity: item.quantity }); setShowEdit(true); }}>
                  Edit
                </button>
              </div>
            </div>
          ))}
        </div>

        <button className="btn add-btn" onClick={() => setShowAdd(true)}>+ Add Item</button>

        {showAdd && (
          <div className="modal-backdrop-custom">
            <div className="modal-content-custom">
              <h5>Add New Item</h5>
              <input className="form-control mb-2" placeholder="Name"
                     value={newItem.name} onChange={e => setNewItem({ ...newItem, name: e.target.value })}/>
              <input className="form-control mb-3" placeholder="Quantity" type="number"
                     value={newItem.quantity} onChange={e => setNewItem({ ...newItem, quantity: e.target.value })}/>
              <button className="btn btn-secondary me-2" onClick={() => setShowAdd(false)} disabled={loading}>Cancel</button>
              <button className="btn btn-primary" onClick={handleAdd} disabled={loading}>{loading ? 'Saving…' : 'Add'}</button>
            </div>
          </div>
        )}

        {showEdit && (
          <div className="modal-backdrop-custom">
            <div className="modal-content-custom">
              <h5>Edit Quantity</h5>
              <input className="form-control mb-3" type="number"
                     value={editItem.quantity} onChange={e => setEditItem({ ...editItem, quantity: e.target.value })}/>
              <button className="btn btn-secondary me-2" onClick={() => setShowEdit(false)} disabled={loading}>Cancel</button>
              <button className="btn btn-primary" onClick={handleEdit} disabled={loading}>{loading ? 'Saving…' : 'Save'}</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SharedSupply;
