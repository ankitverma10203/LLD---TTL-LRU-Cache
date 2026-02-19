# TTL-LRU Cache Implementation

A thread-safe, in-memory cache implementation that combines **Time-To-Live (TTL)** expiration and **Least Recently Used (LRU)** eviction policies.

## Overview

This project implements a low-level design (LLD) of a cache system that manages key-value pairs with two primary features:
- **TTL (Time-To-Live)**: Each cached item automatically expires after a specified duration
- **LRU (Least Recently Used)**: When capacity is reached, the least recently accessed item is removed

## Architecture

### Classes

#### 1. **Node.java**
Represents a single cache entry in a doubly-linked list.

**Properties:**
- `key` & `val`: The key-value pair being cached
- `expiryTime`: When this entry expires (LocalDateTime)
- `next` & `prev`: References to adjacent nodes in the doubly-linked list
- `isExpired()`: Check if the node has exceeded its TTL

**Why Node?**
- The doubly-linked list structure allows O(1) insertion and deletion at any position
- Essential for efficiently managing LRU ordering (most recent at head, least recent at tail)

#### 2. **Cache.java**
The main cache implementation managing all operations.

**Key Features:**
- **Doubly-Linked List**: Maintains insertion/access order for LRU eviction
  - Head: Most recently used item
  - Tail: Least recently used item
- **HashMap (ConcurrentHashMap)**: Provides O(1) lookup of cached values
- **Capacity Management**: Enforces maximum size limit
- **Thread Safety**: Uses `ReentrantLock` to ensure concurrent access safety

**Core Methods:**
- `add(key, val)`: Insert or update a cache entry with renewed TTL
  - If capacity exceeded, evicts LRU entry
  - Updates expiry time on re-insertion
  - Moves entry to head (most recent)
  
- `get(key)`: Retrieve a value by key
  - Returns -1 if key not found
  - Returns -1 if entry has expired
  - Moves accessed entry to head (mark as recently used)

- `evictLRU()`: Removes the least recently used (tail) entry
  - Called when cache reaches capacity
  - Updates size and removes from HashMap

- `moveToHead()`: Relocates a node to the head of the list
  - Used after access/update to reflect LRU ordering

**Why This Design?**
- **ConcurrentHashMap + ReentrantLock**: Ensures thread-safe operations without blocking the entire data structure for every operation
- **Doubly-Linked List**: Supports O(1) reordering without full list traversal
- **TTL Check on Access**: Expired entries are removed lazily (when accessed) rather than with background cleanup threads, reducing overhead

## Technologies Used

| Technology | Purpose | Why Used |
|-----------|---------|----------|
| **Java** | Core language | Type-safe, widely used for system design problems |
| **ConcurrentHashMap** | O(1) key lookups | Thread-safe without locking entire map |
| **ReentrantLock** | Mutual exclusion | Fair lock; prevents race conditions in add/get operations |
| **LocalDateTime** | TTL management | Java standard for time-based expiration tracking |
| **Doubly-Linked List** | LRU ordering | O(1) node repositioning; maintains access order efficiently |

## Time & Space Complexity

| Operation | Time Complexity | Space Complexity |
|-----------|-----------------|------------------|
| `add()` | O(1) | O(1) per entry |
| `get()` | O(1) | O(1) |
| `evictLRU()` | O(1) | - |
| **Overall** | - | O(capacity) |

## Design Patterns

1. **LRU Eviction Policy**: Doubly-linked list tracks access order
2. **TTL Expiration**: Lazy expiration checked during `get()` operations
3. **Thread Safety**: Locking mechanism ensures consistency under concurrent access
4. **Dual Data Structure**: HashMap for speed + Linked List for ordering

## Use Cases

- Session/token caches with automatic expiration
- Database query result caching with capacity constraints
- Rate limiting/throttling systems
- Real-time data caching where stale data must be removed

