import React, { useState } from 'react';
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

    const getItems = async () => {
        setLoading(true);
        setError('');
        try {
            const payload = {
                token: localStorage.getItem('token')
            };
            const res = await fetch("https://roomie.ddns.net/getItems", {
                method: 'POST',
                headers: {'Content-Type': 'application/json'}.
                body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error('Could not load items');
            const data = await res.json();
            setItems(data.items);
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false)
        }
    };

    // On mount, load items
    useEffect(() => {
      getItems();
    }, []);

    const handleAdd = async () => {
        setLoading(true);
        setError('');
        try {
            const payload = {
                token: localStorage.getItem('token'),
                item: newItem.name.trim(),
                quantity: parseInt(newItem.quantity, 10)
            };
            const res = await fetch("https://roomie.ddns.net/addItem", {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error('Add failed');
            await res.json();
            setShowAdd(false);
            setNewItem({ name: '', quantity: '' });
            await getItems();
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false)
        }
    };

    const handleEdit = async () => {
        setLoading(true);
        setError('');
        try {
            const payload = {
                token: localStorage.getItem('token'),
                item: newItem.name.trim(),
                quantity: parseInt(newItem.quantity, 10)
            const res = await fetch("https://roomie.ddns.net/editItem", {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(payload)
            });
            if (!res.ok) throw new Error('Edit failed');
            await res.json();
            setShowEdit(false);
            setEditItem({ id: null, quantity: '' });
            await getItems();
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false)
        }
    };



  return (
    <div className="inventory-container">
      <div className="dashboard-wrapper">
        <RoommateNavBar />
      </div>

      <div className="container py-5">
        <h1 className="text-center mb-4 inventory-title">Shared Supply Inventory</h1>

        {error && <div className="alert alert-danger">{error}</div>}
        {loading && <div className="text-center">Loading…</div>}

        <div className="list-group mb-4">
          {items.map(item => (
            <div key={item.id} className="list-group-item d-flex justify-content-between align-items-center">
              <div>
                <strong>{item.name}</strong> <span className="text-muted">x{item.quantity}</span>
              </div>
              <div>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-secondary me-2"
                  onClick={() => {
                    setEditItem({ id: item.id, quantity: item.quantity });
                    setShowEdit(true);
                  }}
                >
                  Edit
                </button>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-danger"
                  onClick={() => {
                    /* you can call a delete endpoint here then getItems() */
                    setItems(items.filter(i => i.id !== item.id));
                  }}
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>

        <div className="d-flex justify-content-center">
          <button
            type="button"
            className="btn add-btn px-4 py-2"
            onClick={() => setShowAdd(true)}
          >
            + Add Item
          </button>
        </div>
      </div>

      {/* Add Modal */}
      {showAdd && (
        <div className="modal-backdrop-custom">
          <div className="modal-content-custom">
            <h5>Add New Item</h5>
            <input
              type="text"
              className="form-control mb-2"
              placeholder="Item name"
              value={newItem.name}
              onChange={e => setNewItem({ ...newItem, name: e.target.value })}
            />
            <input
              type="number"
              className="form-control mb-3"
              placeholder="Quantity"
              value={newItem.quantity}
              onChange={e => setNewItem({ ...newItem, quantity: e.target.value })}
            />
            <div className="d-flex justify-content-end">
              <button className="btn btn-secondary me-2" onClick={() => setShowAdd(false)} disabled={loading}>
                Cancel
              </button>
              <button className="btn btn-primary" onClick={handleAdd} disabled={loading}>
                {loading ? 'Saving…' : 'Add'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {showEdit && (
        <div className="modal-backdrop-custom">
          <div className="modal-content-custom">
            <h5>Edit Quantity</h5>
            <input
              type="number"
              className="form-control mb-3"
              value={editItem.quantity}
              onChange={e => setEditItem({ ...editItem, quantity: e.target.value })}
            />
            <div className="d-flex justify-content-end">
              <button className="btn btn-secondary me-2" onClick={() => setShowEdit(false)} disabled={loading}>
                Cancel
              </button>
              <button className="btn btn-primary" onClick={handleEdit} disabled={loading}>
                {loading ? 'Saving…' : 'Save'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SharedSupply;
